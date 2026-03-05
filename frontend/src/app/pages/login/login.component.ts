import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  username = '';
  isRegister = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef // ChangeDetector для мгновенного обновления UI
  ) {}

  submit() {
    this.error = '';

    if (this.isRegister) {
      this.authService.register(this.username, this.email, this.password).subscribe({
        next: () => {
          this.router.navigate(['/']); // переход на главную
          this.cdr.detectChanges();   // обновляем шаблон сразу
        },
        error: () => {
          this.error = 'Ошибка регистрации';
          this.cdr.detectChanges();
        }
      });
    } else {
      this.authService.login(this.email, this.password).subscribe({
        next: () => {
          this.router.navigate(['/']);
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'Неверный email или пароль';
          this.cdr.detectChanges();
        }
      });
    }
  }

  toggleMode() {
    this.isRegister = !this.isRegister;
    this.error = '';
    this.cdr.detectChanges();
  }
}
