import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { catchError, of } from 'rxjs';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AuthService } from 'app/auth/data-access/auth.service';

@Component({
  selector: 'app-auth-dialog',
  standalone: true,
  imports: [FormsModule, DialogModule, InputTextModule, ButtonModule, ToastModule],
  templateUrl: './auth-dialog.component.html',
  styleUrls: ['./auth-dialog.component.scss'],
})
export class AuthDialogComponent {
  private readonly auth = inject(AuthService);
  private readonly messageService = inject(MessageService);

  visible = false;
  isLoginMode = signal(true);
  error = signal<string | null>(null);

  showPassword = false;

  username = '';
  firstname = '';
  email = '';
  password = '';

  open() { this.visible = true; this.error.set(null); }
  close() { this.visible = false; }

  toggleMode() {
    this.isLoginMode.update(v => !v);
    this.error.set(null);
  }

  submit() {

    this.error.set(null);

    if (this.isLoginMode()) {
      this.auth.login(this.email, this.password)
        .pipe(
          catchError((err) => {
            const message = err?.error || 'Erreur serveur.';
            this.error.set(message);
            return of(null);
          })
        )
        .subscribe(res => {
          if (res)
            {
              this.messageService.add({
                severity: 'success',
                summary: 'Connexion réussie',
                detail: 'Bienvenue à nouveau !',
                life: 3000
              });
              this.close();
            }
        });

    } else {
      const user = {
        username: this.username || this.email.split('@')[0],
        firstname: this.firstname,
        email: this.email,
        password: this.password,
      };

      this.auth.register(user)
        .pipe(
          catchError((err) => {
            const message = err?.error || 'Erreur serveur.';
            this.error.set(message);
            return of(null);
          })
        )
        .subscribe(res => {
          if (res) {

            this.messageService.add({
              severity: 'success',
              summary: 'Compte créé',
              detail: 'Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.',
              life: 3000
            });

            this.isLoginMode.set(true);
          }
        });
    }
  }
}
