import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, InputTextModule, InputTextareaModule, ButtonModule, ToastModule],
  providers: [MessageService],
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent {
  private readonly fb = inject(FormBuilder);
  private readonly messageService = inject(MessageService);

  readonly form = this.fb.group({
    email: this.fb.control('', { validators: [Validators.required, Validators.email], nonNullable: true }),
    message: this.fb.control('', { validators: [Validators.required, Validators.maxLength(300)], nonNullable: true }),
  });

  readonly submitted = signal(false);

  submit() {
    this.submitted.set(true);
  
    const errs = this.collectErrors();
    if (errs.length) {
      // un toast par erreur, clair et ciblé
      errs.forEach(detail =>
        this.messageService.add({
          severity: 'danger',
          summary: 'Erreur',
          detail,
          life: 3500
        })
      );
  
      // focus sur le 1er champ invalide
      const firstInvalid = this.form.get('email')?.invalid ? 'email' : 'message';
      document.getElementById(firstInvalid)?.focus();
      return;
    }
  
    this.messageService.add({
      severity: 'success',
      summary: 'Succès',
      detail: 'Demande de contact envoyée avec succès.',
      life: 3000
    });
  
    this.form.reset();
    this.submitted.set(false);
  }
  
  private collectErrors(): string[] {
    const e = this.form.controls.email;
    const m = this.form.controls.message;
  
    const errors: string[] = [];
  
    if (e.errors) {
      if (e.errors['required']) errors.push("L'email est obligatoire.");
      if (e.errors['email']) errors.push("L'email n'est pas valide.");
    }
  
    if (m.errors) {
      if (m.errors['required']) errors.push('Le message est obligatoire.');
      if (m.errors['maxlength']) errors.push('Le message ne doit pas dépasser 300 caractères.');
    }
  
    return errors;
  }
  
}
