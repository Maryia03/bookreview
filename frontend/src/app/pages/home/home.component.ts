import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../core/services/book.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{
  books: any[] = [];
  search = '';

  constructor(private bookService: BookService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void{
    this.loadBooks();
  }

  loadBooks(){
    this.bookService.getAllBooks().subscribe(data => {
      this.books = data;
      this.cdr.detectChanges();
    });
  }

  onSearch(){
    this.bookService.searchBooks(this.search).subscribe(data => {
      this.books = data;
      this.cdr.detectChanges();
    });
  }
}
