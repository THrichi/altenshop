import {
  Component,
  computed,
  EventEmitter,
  input,
  Output,
  ViewEncapsulation,
} from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Product } from "app/products/data-access/product.model";
import { ButtonModule } from "primeng/button";
import { InputNumberModule } from "primeng/inputnumber";
import { InputTextModule } from "primeng/inputtext";
import { InputTextareaModule } from "primeng/inputtextarea";
import { DropdownModule } from "primeng/dropdown";

@Component({
  selector: "app-product-form",
  template: `
    <form #form="ngForm" (ngSubmit)="onSave()" class="p-fluid flex flex-column gap-3">

      <div class="form-field">
        <label for="name">Nom</label>
        <input pInputText id="name" name="name" type="text" [(ngModel)]="editedProduct().name" required />
      </div>

      <div class="form-field">
        <label for="code">Code</label>
        <input pInputText id="code" name="code" type="text" [(ngModel)]="editedProduct().code" required />
      </div>

      <div class="form-field">
        <label for="description">Description</label>
        <textarea pInputTextarea id="description" name="description" rows="3" [(ngModel)]="editedProduct().description"></textarea>
      </div>

      <div class="form-field">
        <label for="image">Image (URL)</label>
        <input pInputText id="image" name="image" type="text" [(ngModel)]="editedProduct().image" placeholder="https://..." />
      </div>

      <div class="form-field">
        <label for="category">Catégorie</label>
        <input pInputText id="category" name="category" type="text" [(ngModel)]="editedProduct().category" />
      </div>

      <div class="form-field">
        <label for="price">Prix (€)</label>
        <p-inputNumber [(ngModel)]="editedProduct().price" name="price" mode="decimal" minFractionDigits="2" required />
      </div>

      <div class="form-field">
        <label for="quantity">Quantité</label>
        <p-inputNumber [(ngModel)]="editedProduct().quantity" name="quantity" [min]="0" />
      </div>

      <div class="form-field">
        <label for="internalReference">Référence interne</label>
        <input pInputText id="internalReference" name="internalReference" type="text" [(ngModel)]="editedProduct().internalReference" />
      </div>

      <div class="form-field">
        <label for="shellId">Shell ID</label>
        <p-inputNumber [(ngModel)]="editedProduct().shellId" name="shellId" [min]="0" />
      </div>

      <div class="form-field">
        <label for="inventoryStatus">Statut du stock</label>
        <p-dropdown [options]="inventoryOptions" [(ngModel)]="editedProduct().inventoryStatus" name="inventoryStatus" appendTo="body" />
      </div>

      <div class="form-field">
        <label for="rating">Note</label>
        <p-inputNumber [(ngModel)]="editedProduct().rating" name="rating" [min]="0" [max]="5" />
      </div>

      <div class="flex justify-content-between mt-3">
        <p-button type="button" (onClick)="onCancel()" label="Annuler" severity="help" />
        <p-button type="submit" [disabled]="!form.valid" label="Enregistrer" severity="success" />
      </div>
    </form>
  `,
  styleUrls: ["./product-form.component.scss"],
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    InputTextareaModule,
    DropdownModule,
  ],
  encapsulation: ViewEncapsulation.None,
})
export class ProductFormComponent {
  public readonly product = input.required<Product>();

  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<Product>();

  public readonly editedProduct = computed(() => ({ ...this.product() }));

  public readonly inventoryOptions = [
    { value: "INSTOCK", label: "In stock" },
    { value: "LOWSTOCK", label: "Low stock" },
    { value: "OUTOFSTOCK", label: "Out of stock" },
  ];

  onCancel() {
    this.cancel.emit();
  }

  onSave() {
    this.save.emit(this.editedProduct());
  }
}
