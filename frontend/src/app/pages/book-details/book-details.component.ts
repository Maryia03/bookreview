import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BookService, BookDetails, CommentDTO } from '../../core/services/book.service';
import { UserService, User } from '../../core/services/user.service';
import { AdminService } from '../../core/services/admin.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './book-details.component.html',
  styleUrls: ['./book-details.component.scss']
})
export class BookDetailsComponent implements OnInit {
  book: BookDetails | null = null;
  comments: CommentDTO[] = [];
  user: User | null = null;
  activeMenuCommentId: number | null = null;
  page = 0;
  sortBy = 'new';
  loading = false;
  newComment = '';
  rating = 0;
  editMode = false;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private adminService: AdminService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.loadBook(id);
    this.userService.getCurrentUser().subscribe({
      next: u => {
        this.user = u;
        this.cdr.detectChanges();
      },
      error: err => console.error('Failed to load user', err)
    });
  }

  loadBook(id: number): void {
    this.bookService.getBookDetails(id, this.sortBy).subscribe({
      next: data => {
        this.book = data;
        this.comments = data.comments || [];
        this.page = data.currentPage || 0;
        if (this.book.coverUrl === 'null') this.book.coverUrl = '';
        this.cdr.detectChanges();
      },
      error: err => console.error('Failed to load book details', err)
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.comment-menu')) {
      this.activeMenuCommentId = null;
    }
  }

  get fullStars(): number[] {
    return Array.from({ length: 10 }, (_, i) => i + 1);
  }

  get userRating(): number {
    return this.book?.userRating ?? 0;
  }

  submitHalfOrFullRating(event: MouseEvent, starNumber: number): void {
    if (!this.book) return;
    const target = event.target as HTMLElement;
    const { left, width } = target.getBoundingClientRect();
    const clickX = event.clientX - left;
    const isHalf = clickX < width / 2;
    const rating = isHalf ? starNumber - 0.5 : starNumber;
    this.bookService.rateBook(this.book.id, rating).subscribe({
      next: () => this.loadBook(this.book!.id),
      error: err => console.error('Failed to rate book', err)
    });
  }

  changeSort(sort: string): void {
    this.sortBy = sort;
    this.page = 0;
    if (this.book) this.loadBook(this.book.id);
  }

  loadMore(): void {
    if (!this.book) return;
    this.loading = true;
    this.bookService.getComments(this.book.id, this.page + 1, 10, this.sortBy).subscribe({
      next: res => {
        this.comments = [...this.comments, ...(res.content || [])];
        this.page = res.number ?? (this.page + 1);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Failed to load more comments', err);
        this.loading = false;
      }
    });
  }

  submitComment(): void {
    if (!this.book || !this.newComment.trim()) return;
    this.bookService.addComment(this.book.id, this.newComment).subscribe({
      next: comment => {
        this.comments = [comment, ...this.comments];
        this.newComment = '';
        this.cdr.detectChanges();
      },
      error: err => console.error('Failed to add comment', err)
    });
  }

  likeComment(comment: CommentDTO): void {
    this.bookService.likeComment(comment.id).subscribe({
      next: updated => {
        this.comments = this.comments.map(c => c.id === updated.id ? updated : c);
        this.cdr.detectChanges();
      },
      error: err => console.error('Failed to like comment', err)
    });
  }

  dislikeComment(comment: CommentDTO): void {
    this.bookService.dislikeComment(comment.id).subscribe({
      next: updated => {
        this.comments = this.comments.map(c => c.id === updated.id ? updated : c);
        this.cdr.detectChanges();
      },
      error: err => console.error('Failed to dislike comment', err)
    });
  }

  toggleMenu(commentId: number) {
    this.activeMenuCommentId = this.activeMenuCommentId === commentId ? null : commentId;
  }

  deleteComment(commentId: number) {
    this.adminService.deleteComment(commentId).subscribe(() => {
      this.comments = this.comments.filter(c => c.id !== commentId);
      this.activeMenuCommentId = null;
      this.cdr.detectChanges();
    });
  }

  blockUser(userId: number) {
    this.adminService.blockUser(userId).subscribe(() => {
      alert("User blocked");
      this.activeMenuCommentId = null;
    });
  }

  updateBook(): void {
    if (!this.book) return;
    this.adminService.updateBook(this.book.id, this.book).subscribe(() => {
      this.editMode = false;
      alert("Book updated");
      this.cdr.detectChanges();
    });
  }
}
