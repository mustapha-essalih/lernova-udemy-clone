import Link from "next/link";
import { StatusContainer } from "./StatusContainer";
import { RxCrossCircled } from "react-icons/rx";
import { CircularProgress } from "@mui/material";

export function Loading() {
  return (
    <StatusContainer>
      <div className="bg-emerald-500 w-[60px] aspect-square flex items-center justify-center rounded-full absolute left-1/2 top-[-30px] translate-x-[-50%] text-white ">
        <CircularProgress color="inherit" size={28} thickness={4.9} />
      </div>
      <h1 className="text-3xl font-medium">Email Verification</h1>
      <p>We are verifying your email...</p>
    </StatusContainer>
  );
}