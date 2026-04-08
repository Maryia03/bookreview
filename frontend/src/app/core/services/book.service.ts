import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CommentDTO {
  id: number;
    username: string;
    content: string;
    userId: number;
    avatarUrl?: string;
    likesCount: number;
    dislikesCount: number;
    userReaction: string | null;
    bookId: number;
    bookTitle: string;
}

export interface BookDetails{
  id: number;
  title: string;
  author: string;
  description: string;
  coverUrl: string | null;
  averageRating: number;
  ratingsCount: number;
  userRating: number | null;
  comments: CommentDTO[];
  totalPages: number;
  currentPage: number;
}

export interface RatingDTO{
  id: number;
  bookId: number;
  bookTitle: string;
  value: number;
}

@Injectable({ providedIn: 'root' })
export class BookService {
  private api = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getAllBooks(): Observable<any[]>{
    return this.http.get<any[]>(`${this.api}/books`);
  }

  getBookDetails(id: number, sortBy: string = 'new'): Observable<BookDetails>{
    return this.http.get<BookDetails>(`${this.api}/books/${id}?sortBy=${sortBy}`);
  }

  rateBook(bookId: number, score: number): Observable<any>{
    return this.http.post(`${this.api}/books/${bookId}/ratings`, { score });
  }

  getComments(bookId: number, page = 0, size = 10, sortBy = 'new'): Observable<any>{
    return this.http.get<any>(
      `${this.api}/books/${bookId}/comments?page=${page}&size=${size}&sortBy=${sortBy}`
    );
  }

  addComment(bookId: number, content: string): Observable<CommentDTO>{
    return this.http.post<CommentDTO>(`${this.api}/books/${bookId}/comments`, { content });
  }

  likeComment(commentId: number){
    return this.http.post<any>(`http://localhost:8080/api/comments/${commentId}/like`, {});
  }

  dislikeComment(commentId: number){
    return this.http.post<any>(`http://localhost:8080/api/comments/${commentId}/dislike`, {});
  }

  searchBooks(query: string): Observable<any[]> {
    if (!query.trim()) return this.getAllBooks();
    return this.http.get<any[]>(`${this.api}/books/search?query=${encodeURIComponent(query)}`);
  }
}
