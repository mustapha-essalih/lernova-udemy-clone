import Link from "next/link";

import SearchBar from "./SearchBar";

export default function Navbar() {
  return (
    <nav className="flex items-center justify-between h-20 md:px-12 px-5 bg-white w-full">
      <Link href="/" className="font-bitcount text-3xl text-black font-medium">Learnova</Link>
      <SearchBar />
      <div className="flex items-center gap-5">
        <Link href="/login" className="text-black font-medium">Login</Link>
        <Link href="/signup" className="bg-emerald-500 text-white px-4 py-2 rounded-full font-medium">Sign Up</Link>
      </div>
    </nav>
  );
}