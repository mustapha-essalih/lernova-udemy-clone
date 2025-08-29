'use client';

import { useForm } from "react-hook-form";
import { useState } from "react";
import { StatusContainer } from "./StatusContainer";
import { RiErrorWarningLine } from "react-icons/ri";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";

const formSchema = z.object({
  email: z.email("Invalid email address"),
})

export function Expired() {
  const [open, setOpen] = useState(false);
  const { register, handleSubmit, setError, formState:{errors} } = useForm<{email: string}>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
    }
  });

  const onSubmit = async ({email}: {email: string}) => {
    try {
      await axios.post(`http://localhost:8081/api/v1/auth/resend-verification-email`, {email});
    } catch (e) {
      setError("email", {message: "Failed to send email. Please try again later."});
      console.log(e);
    }
  }

  return (
    <StatusContainer>
      <div className="bg-orange-500 w-[60px] aspect-square flex items-center justify-center rounded-full absolute left-1/2 top-[-30px] translate-x-[-50%] ">
        <RiErrorWarningLine className="text-white text-4xl" />
      </div>
      <h1 className="text-3xl font-medium">Email Verification</h1>
      <p>Your verification link has expired. Please request a new one.</p>
      {
        !open && <button type="button" onClick={() => setOpen(true)} className="bg-orange-500 py-2 px-8 rounded-full text-white font-medium text-lg cursor-pointer">Request new open</button>
      }
      {
        open && <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col items-center gap-4 w-full max-w-[400px]">
          <input {...register('email')} type="email" placeholder="Enter your email" className="border border-gray-300 rounded-full py-2 px-4 w-full focus:outline-none focus:ring-2 focus:ring-orange-500" />
          {
            errors.email && <p className="text-red-500 text-sm mr-auto">{errors.email.message}</p>
          }
          {
            errors.root && <p className="text-red-500 text-sm mr-auto">{errors.root.message}</p>
          }
          <button type="submit" className="bg-orange-500 py-2 px-8 w-fit rounded-full text-white font-medium text-lg cursor-pointer">Send</button>
        </form>
      }
    </StatusContainer>
  );
}