import Link from "next/link";

import { IoSearch } from "react-icons/io5";

export default function Navbar() {
  return (
    <nav className="flex items-center justify-between h-20 md:px-12 px-5 bg-white w-full">
      <Link href="/" className="font-bitcount text-3xl text-black font-medium">Learnova</Link>
      <div className="md:flex p-1 rounded-full gap-2 lg:w-[70%] md:w-1/2 hidden border border-black ">
        <div className="flex items-center justify-center w-8 aspect-square bg-emerald-500 rounded-full">
          <IoSearch className="text-white text-xl" />
        </div>
        <input type="text" placeholder="what do you want to learn ?" className="w-full border-none outline-none" />
      </div>
      <div className="flex items-center gap-5">
        <Link href="/login" className="text-black font-medium">Login</Link>
        <Link href="/signup" className="bg-emerald-500 text-white px-4 py-2 rounded-full font-medium">Sign Up</Link>
      </div>
    </nav>
  );
}