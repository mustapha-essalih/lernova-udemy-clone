'use client';

import { useForm } from "react-hook-form";
import { useState } from "react";
import { StatusContainer } from "./StatusContainer";
import { RiErrorWarningLine } from "react-icons/ri";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { CircularProgress } from "@mui/material";
import Notification from "./EmailVerificationNotification";

const formSchema = z.object({
  email: z.email("Invalid email address"),
})

export function Expired() {
  const [open, setOpen] = useState(false);
  const [emailSent, setEmailSent] = useState(false);
  const [displayNotification, setDisplayNotification] = useState(false);
  const { register, handleSubmit, setError, formState:{errors, isSubmitting} } = useForm<{email: string}>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
    }
  });

  const onSubmit = async ({email}: {email: string}) => {
    if (emailSent) return;
    try {
      await axios.post(`http://localhost:8081/api/v1/auth/resend-email-verification`, {email});

      setEmailSent(true);
      setDisplayNotification(true);
      setTimeout(() => {
        setDisplayNotification(false);
      }, 5000);
    } catch (e) {
      setError("email", {message: "Failed to send email. Please try again later."});
      console.log(e);
    }
  }

  return (
    <>
      <StatusContainer>
        <div className="bg-orange-500 w-[60px] aspect-square flex items-center justify-center rounded-full absolute left-1/2 top-[-30px] translate-x-[-50%] ">
          <RiErrorWarningLine className="text-white text-4xl" />
        </div>
        <h1 className="text-3xl font-medium">Email Verification</h1>
        <p>Your verification link has expired. Please request a new one.</p>
        {
          !open && <button type="button" onClick={() => setOpen(true)} className="bg-orange-500 py-2 px-8 rounded-full text-white font-medium text-lg cursor-pointer">Request new one</button>
        }
        {
          !emailSent && open && <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col items-center gap-4 w-full max-w-[400px]">
            <input {...register('email')} type="email" disabled={emailSent} placeholder="Enter your email" className="border border-gray-300 rounded-full py-2 px-4 w-full focus:outline-none focus:ring-2 focus:ring-orange-500" />
            {
              errors.email && <p className="text-red-500 text-sm mr-auto">{errors.email.message}</p>
            }
            <button type="submit" disabled={isSubmitting} className="bg-orange-500 w-[105px] h-[44px] rounded-full text-white font-medium text-lg cursor-pointer flex items-center justify-center">
              {
                isSubmitting ? <CircularProgress size={24} thickness={4.9} color="inherit"  /> : "Send"
              }
            </button>
          </form>
        }
      </StatusContainer>
      {
        displayNotification && <Notification />
      }
    </>
  );
}