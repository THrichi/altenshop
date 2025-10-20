import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, Routes } from "@angular/router";
import { ProductListComponent } from "./features/product-list/product-list.component";
import { ContactComponent } from "app/contact/ui/contact/contact.component";
import { WishlistComponent } from "./whishlist/ui/wishlist/wishlist.component";
import { CartComponent } from "./cart/ui/cart/cart.component";

export const PRODUCTS_ROUTES: Routes = [
	{
		path: "list",
		component: ProductListComponent,
	},
	{
		path: "favoris",
		component: WishlistComponent,
	},
	{
		path: "cart",
		component: CartComponent,
	},
	{ path: "**", redirectTo: "list" },
];
