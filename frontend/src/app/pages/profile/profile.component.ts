import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../core/services/user.service';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})

export class ProfileComponent implements OnInit{
  user: any;
  ratedBooks: any[] = [];
  comments: any[] = [];
  editing = false;

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  saveProfile(){
    this.userService.updateProfile({
      username: this.user.username,
      avatarUrl: this.user.avatarUrl
    }).subscribe(updated => {
      this.user = updated;
      this.editing = false;
    });
  }

  ngOnInit(): void{
    this.loadProfile();
  }

  deleteComment(id: number){
    this.userService.deleteComment(id).subscribe(() => {
      this.comments = this.comments.filter(c => c.id !== id);
    });
  }

  loadProfile(){
      this.userService.getCurrentUser().subscribe(data => {
        this.user = data;
        this.cdr.detectChanges();
        this.loadUserActivity();
      });
    }

  loadUserActivity(){
      this.userService.getUserRatings().subscribe(data => {
        this.ratedBooks = data;
        this.cdr.detectChanges();
      });

      this.userService.getUserComments().subscribe(data =>{
        this.comments = data;
        this.cdr.detectChanges();
      });
    }
  }
