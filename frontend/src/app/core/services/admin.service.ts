import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class AdminService{
  private api = 'http://localhost:8080/api/admin';
  constructor(private http: HttpClient) {}

  //books
  createBook(book: any){
      return this.http.post(`${this.api}/books`, book);
    }

  getAllBooks(){
    return this.http.get<any[]>(`${this.api}/books`);
  }

  deleteBook(id: number){
    return this.http.delete(`${this.api}/books/${id}`);
  }

  updateBook(id: number, data: any){
    return this.http.put(`${this.api}/books/${id}`, data);
  }

  //users
  getAllUsers(){
    return this.http.get<any[]>(`${this.api}/users`);
  }

  blockUser(id: number) {
    return this.http.put<{blocked: boolean}>(`${this.api}/users/${id}/block`, {});
  }

  unblockUser(id: number) {
    return this.http.put<{blocked: boolean}>(`${this.api}/users/${id}/unblock`, {});
  }

  deleteComment(id: number) {
    return this.http.delete(`${this.api}/comments/${id}`);
  }
}
