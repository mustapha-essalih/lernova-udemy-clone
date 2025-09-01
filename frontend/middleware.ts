// middleware.ts
import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { getUser } from "./lib/auth";

export async function middleware(req: NextRequest) {
  const user = await getUser(req);

  // const url = req.nextUrl.clone();

  // // Block if not logged in
  // if (!user) {
  //   url.pathname = "/login";
  //   return NextResponse.redirect(url);
  // }

  // // Protect instructor routes
  // if (url.pathname.startsWith("/instructor")) {
  //   if (user.role !== "INSTRUCTOR") {
  //     url.pathname = "/403"; // forbidden page
  //     return NextResponse.redirect(url);
  //   }
  // }

  // // Protect admin routes
  // if (url.pathname.startsWith("/admin")) {
  //   if (user.role !== "ADMIN") {
  //     url.pathname = "/403";
  //     return NextResponse.redirect(url);
  //   }
  // }

  return NextResponse.next();
}

export const config = {
  matcher: ["/instructor/:path*", "/admin/:path*"], // apply only to these
};
