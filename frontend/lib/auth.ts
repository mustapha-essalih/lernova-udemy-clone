import { jwtVerify } from "jose";
import { NextRequest } from "next/server";

export async function getUser(req: NextRequest) {
  const token = req.cookies.get("token")?.value;

  if (!token) return null;
  try {
    jwtVerify(token, )
  }
}