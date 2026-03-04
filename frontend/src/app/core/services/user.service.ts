import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class UserService{
  private api = 'http://localhost:8080/api/users';
  constructor(private http: HttpClient) {}

  getCurrentUser(){
    return this.http.get<any>(`${this.api}/me`);
  }

  updateProfile(data: { username: string; avatarUrl: string }){
    return this.http.put<any>(`${this.api}/me`, data);
  }

  getUserRatings(){
    return this.http.get<any[]>(`${this.api}/ratings`);
  }

  getUserComments(){
    return this.http.get<any[]>(`${this.api}/comments`);
  }

  deleteComment(id: number){
    return this.http.delete(`http://localhost:8080/api/comments/${id}`);
  }
}
