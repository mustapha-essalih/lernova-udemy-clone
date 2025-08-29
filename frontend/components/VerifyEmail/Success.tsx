import { CgCheckO } from "react-icons/cg";
import Link from "next/link";
import { StatusContainer } from "./StatusContainer";

export function Success({msg}: {msg: string}) {
  return (
    <StatusContainer>
      <div className="bg-emerald-500 w-[60px] aspect-square flex items-center justify-center rounded-full absolute left-1/2 top-[-30px] translate-x-[-50%] ">
        <CgCheckO className="text-white text-4xl" />
      </div>
      <h1 className="text-3xl font-medium">Email Verification</h1>
      <p>{msg}</p>
      <Link href="/login" className="bg-emerald-500 py-2 px-8 rounded-full text-white font-medium text-lg">Login</Link>
    </StatusContainer>
  );
}