import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { BookService } from '../../core/services/book.service';
import { BookDetails, CommentDTO } from '../../core/models/book-details.model';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [CommonModule, HttpClientModule, RouterModule],
  templateUrl: './book-details.component.html',
  styleUrls: ['./book-details.component.scss']
})
export class BookDetailsComponent implements OnInit {
  book: BookDetails | null = null;
  comments: CommentDTO[] = [];
  page = 0;
  sortBy = 'new';
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef // добавляем ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = Number(idParam);
    if (!id) {
      console.error('Book id is missing!');
      return;
    }
    this.loadBook(id);
  }

  loadBook(id: number): void {
    this.bookService.getBookDetails(id, this.sortBy).subscribe({
      next: data => {
        this.book = data;
        if (this.book.coverUrl === 'null') this.book.coverUrl = '';
        this.comments = data.comments || [];
        this.page = data.currentPage || 0;
        this.cdr.detectChanges(); // обновляем UI сразу
        console.log('Book loaded:', data);
      },
      error: err => {
        console.error('Failed to load book details', err);
      }
    });
  }

  loadMore(): void {
    if (!this.book) return;
    const id = this.book.id;
    this.loading = true;

    this.bookService.getComments(id, this.page + 1, 10, this.sortBy).subscribe({
      next: res => {
        this.comments = [...this.comments, ...(res.content || [])];
        this.page = res.number ?? (this.page + 1);
        this.loading = false;
        this.cdr.detectChanges(); // обновляем UI после добавления комментариев
      },
      error: err => {
        console.error('Failed to load more comments', err);
        this.loading = false;
      }
    });
  }

  changeSort(sort: string): void {
    this.sortBy = sort;
    this.page = 0;
    if (this.book) {
      this.loadBook(this.book.id);
    }
  }
}
