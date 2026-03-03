import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { BookDetails } from '../models/book-details.model';
import { Observable } from 'rxjs';

@Injectable({providedIn: 'root'})

export class BookService{
  private api = 'http://localhost:8080/api/books';
  constructor(private http: HttpClient){}

  getAllBooks(){
      return this.http.get<any[]>(this.api);
    }

    getBookDetails(id: number, sortBy: string = 'new'): Observable<BookDetails> {
      return this.http.get<BookDetails>(`${this.api}/${id}?sortBy=${sortBy}`);
    }

    rateBook(bookId: number, score: number){
      return this.http.post(`${this.api}/${bookId}/ratings`, { score });
    }

    getComments(bookId: number, page = 0, size = 10, sortBy = 'new'){
      return this.http.get<any>(`http://localhost:8080/api/books/${bookId}/comments?page=${page}&size=${size}&sortBy=${sortBy}`);
    }

    addComment(bookId: number, content: string){
      return this.http.post(`http://localhost:8080/api/books/${bookId}/comments`,{ content });
    }
    searchBooks(query: string): Observable<any[]>{
        if (!query.trim()){
          return this.getAllBooks();
        }
        return this.http.get<any[]>(`${this.api}?search=${encodeURIComponent(query)}`);
      }
  }
