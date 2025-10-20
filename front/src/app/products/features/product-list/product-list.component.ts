import { Component, OnInit, inject, signal } from "@angular/core";
import { Product } from "app/products/data-access/product.model";
import { ProductsService } from "app/products/data-access/products.service";
import { ProductFormComponent } from "app/products/ui/product-form/product-form.component";
import { ButtonModule } from "primeng/button";
import { CardModule } from "primeng/card";
import { DataViewModule } from 'primeng/dataview';
import { DialogModule } from 'primeng/dialog';
import { PaginatorModule } from 'primeng/paginator';
import { viewChild } from '@angular/core';
import { AuthDialogComponent } from "app/auth/ui/auth-dialog/auth-dialog.component";
import { AuthService } from "app/auth/data-access/auth.service";
import { ViewportScroller } from "@angular/common";
import { ToastModule } from "primeng/toast";
import { MessageService } from "primeng/api";
import { catchError, of } from "rxjs";
import { ConfirmationService } from "primeng/api";
import { ConfirmDialogModule } from "primeng/confirmdialog";
import { InputNumberModule } from "primeng/inputnumber";
import { FormsModule } from "@angular/forms";
import { WishlistService } from "app/products/whishlist/data-access/wishlist.service";
import { CartService } from "app/products/cart/data-access/cart.service";


const emptyProduct: Product = {
  id: 0,
  code: "",
  name: "",
  description: "",
  image: "",
  category: "",
  price: 0,
  quantity: 0,
  internalReference: "",
  shellId: 0,
  inventoryStatus: "INSTOCK",
  rating: 0,
  createdAt: 0,
  updatedAt: 0,
};

@Component({
  selector: "app-product-list",
  templateUrl: "./product-list.component.html",
  styleUrls: ["./product-list.component.scss"],
  standalone: true,
  imports: [DataViewModule, CardModule, ButtonModule, DialogModule, ProductFormComponent,PaginatorModule,
    AuthDialogComponent, ToastModule,ConfirmDialogModule,InputNumberModule,FormsModule],
})
export class ProductListComponent implements OnInit {
  private readonly scroller = inject(ViewportScroller);
  private readonly productsService = inject(ProductsService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);
  public  readonly authService = inject(AuthService);
  public  readonly wishlistService = inject(WishlistService);
  public readonly cartService = inject(CartService);

  public user = this.authService.user();
  authDialog = viewChild.required(AuthDialogComponent);

  public readonly products = this.productsService.products;
  public quantities: Record<number, number> = {};

  public isDialogVisible = false;
  public isCreation = false;
  public readonly editedProduct = signal<Product>(emptyProduct);

  public totalRecords = signal(0);
  public rows = signal(5);
  public currentPage = signal(0);

  ngOnInit() {
    this.loadProducts(0);
    this.wishlistService.get().subscribe();
  }

  public loadProducts(page: number) {
    this.productsService.get(page, this.rows()).subscribe((res) => {
      this.totalRecords.set(res.totalElements);
      console.log('number ->', res.number);
      this.currentPage.set(res.number);
    });
  }
  

  public onPageChange(event: any) {
    this.currentPage.set(event.page);
    this.rows.set(event.rows);
    this.loadProducts(event.page);
  }

  public onCreate() {
    this.isCreation = true;
    this.isDialogVisible = true;
    this.editedProduct.set(emptyProduct);
  }

  public onUpdate(product: Product) {
    this.isCreation = false;
    this.isDialogVisible = true;
    this.editedProduct.set(product);
  }

  public onDelete(product: Product) {
    this.confirmationService.confirm({
      header: 'Confirmation',
      message: `Voulez-vous vraiment supprimer le produit "${product.name}" ?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Supprimer',
      rejectLabel: 'Annuler',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        this.productsService.delete(product.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Supprimé',
              detail: 'Produit supprimé avec succès.',
              life: 3000,
            });
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: err?.error || 'Échec de la suppression du produit.',
              life: 4000,
            });
          },
        });
      },
    });
  }
  

  public onSave(product: Product) {
    if (this.isCreation) {
      this.productsService.create(product)
        .pipe(
          catchError((err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: err?.error || 'Échec de la création du produit.',
              life: 4000,
            });
            return of(null);
          })
        )
        .subscribe((res) => {
          if (res) {
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Produit créé avec succès.',
              life: 3000,
            });
            this.closeDialog();
          }
        });
    } else {
      this.productsService.update(product)
        .pipe(
          catchError((err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: err?.error || 'Échec de la modification du produit.',
              life: 4000,
            });
            return of(null);
          })
        )
        .subscribe((res) => {
          if (res) {
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Produit modifié avec succès.',
              life: 3000,
            });
            this.closeDialog();
          }
        });
    }
  }

  public onCancel() {
    this.closeDialog();
  }

  private closeDialog() {
    this.isDialogVisible = false;
  }

  public onAddToCart(product: Product): void {
    if (!this.authService.isLoggedIn()) {
      this.authDialog().open();
      return;
    }
  
    const quantity = this.quantities[product.id] || 1;
  
    this.cartService.add(product.id, quantity).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Ajouté au panier',
          detail: `${product.name} (${quantity}) a été ajouté à votre panier.`,
          life: 3000,
        });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: err?.error || 'Impossible d’ajouter le produit au panier.',
          life: 4000,
        });
      },
    });
  }
  
  
  public onAddToFavorites(product: Product): void {
    if (!this.authService.isLoggedIn()) {
      this.authDialog().open(); 
      return;
    }
    
    const isFav = this.wishlistService.isFavorite(product.id);

    this.wishlistService.toggle(product.id);

    this.messageService.add({
      severity: isFav ? 'warn' : 'success',
      summary: isFav ? 'Retiré' : 'Ajouté',
      detail: isFav
        ? `${product.name} a été retiré de vos favoris.`
        : `${product.name} a été ajouté à vos favoris.`,
      life: 3000,
    });

  }
  

  public formatInventoryStatus(status: 'INSTOCK' | 'LOWSTOCK' | 'OUTOFSTOCK'): string {
    switch (status) {
      case 'INSTOCK':
        return 'In stock';
      case 'LOWSTOCK':
        return 'Low stock';
      case 'OUTOFSTOCK':
        return 'Out of stock';
      default:
        return '';
    }
  }
  
  
}
