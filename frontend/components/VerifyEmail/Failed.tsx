import Link from "next/link";
import { StatusContainer } from "./StatusContainer";
import { RxCrossCircled } from "react-icons/rx";

export function Failed() {
  return (
    <StatusContainer>
      <div className="bg-red-500 w-[60px] aspect-square flex items-center justify-center rounded-full absolute left-1/2 top-[-30px] translate-x-[-50%] ">
        <RxCrossCircled className="text-white text-4xl" />
      </div>
      <h1 className="text-3xl font-medium">Email Verification</h1>
      <p>Failed to verify email. Please try again later.</p>
      <Link href="/login" className="bg-red-500 py-2 px-8 rounded-full text-white font-medium text-lg">Login</Link>
    </StatusContainer>
  );
}