import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin.service';

export interface Book {
  id: number;
  title: string;
  author: string;
  description: string;
  coverUrl: string;
  averageRating: number;
  ratingsCount: number;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  books: Book[] = [];
  users: any[] = [];
  newBook: Partial<Book> = {};
  showAddBookModal = false;

  constructor(private adminService: AdminService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(){
    this.adminService.getAllBooks().subscribe(data =>{
      this.books = data;
      this.cdr.detectChanges();
    });
    this.adminService.getAllUsers().subscribe(data =>{
      this.users = data;
      this.cdr.detectChanges();
    });
  }

  // Books
  createBook(event: Event){
    event.preventDefault();
    if (!this.newBook.title || !this.newBook.author) return;
    this.adminService.createBook(this.newBook).subscribe(() => {
      this.newBook = {};
      this.showAddBookModal = false;
      this.loadData();
    });
  }

  deleteBook(id: number){
    this.adminService.deleteBook(id).subscribe(() => this.loadData());
  }

  closeModal(){
    this.showAddBookModal = false;
    this.newBook = {};
  }


  toggleBlockUser(user: any){
    if (user.blocked) {
      this.adminService.unblockUser(user.id).subscribe(res =>{
        this.users = this.users.map(u => u.id === user.id ? { ...u, blocked: res.blocked } : u);
        this.cdr.detectChanges();
      });
    }else{
      this.adminService.blockUser(user.id).subscribe(res =>{
        this.users = this.users.map(u => u.id === user.id ? { ...u, blocked: res.blocked } : u);
        this.cdr.detectChanges();
      });
    }
  }
}
