import { Component, computed, inject, ViewChild } from "@angular/core";
import { AuthService } from "app/auth/data-access/auth.service";
import { AuthDialogComponent } from "app/auth/ui/auth-dialog/auth-dialog.component";
import { CartService } from "app/products/cart/data-access/cart.service";
import { MenuItem } from "primeng/api";
import { PanelMenuModule } from "primeng/panelmenu";

@Component({
  selector: "app-panel-menu",
  standalone: true,
  imports: [PanelMenuModule, AuthDialogComponent],
  template: `
    <p-panelMenu [model]="menuItems()" styleClass="w-full" />
    <app-auth-dialog #authDialog></app-auth-dialog>
  `
})
export class PanelMenuComponent {
  private readonly auth = inject(AuthService);
  private readonly cartService = inject(CartService);
  cartCount = computed(() => this.cartService.items().length);

  @ViewChild("authDialog") authDialog!: AuthDialogComponent;

  readonly menuItems = computed<MenuItem[]>(() => {
    const items: MenuItem[] = [
      { label: "Accueil", icon: "pi pi-home", routerLink: ["/home"] },
      { label: "Produits", icon: "pi pi-barcode", routerLink: ["/products/list"] }
    ];

    const user = this.auth.user();

    if (user) {
      items.push({
        label: user.username,
        icon: "pi pi-user",
        items: [
          { label: "Mes favoris", icon: "pi pi-heart", routerLink: ["/products//favoris"] },
          {
            label: `Mon panier <span class="cart-badge">${this.cartCount()}</span>`,
            icon: "pi pi-shopping-cart",
            routerLink: ["/products/cart"],
            escape: false
          }
          ,
          {
            label: "Déconnexion",
            icon: "pi pi-sign-out",
            command: () => this.auth.logout()
          }
        ]
      });
    } else {
      items.push({
        label: "Connexion",
        icon: "pi pi-sign-in",
        command: () => this.authDialog.open()
      });
    }

    items.push(
      { label: "──────────", disabled: true, style: { pointerEvents: "none", opacity: "0.4" } },
      { label: "Contact", icon: "pi pi-envelope", routerLink: ["/contact"] }
    );

    return items;
  });
}
