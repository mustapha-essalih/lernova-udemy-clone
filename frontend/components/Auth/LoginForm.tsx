'use client';

import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { FaGoogle } from "react-icons/fa";
import { useForm } from "react-hook-form";

import AuthInput from "../ui/AuthInput";

const loginSchema = z.object({
  email: z.email("Invalid email address"),
  password: z.string().min(8, "Password must be at least 8 characters long"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginForm() {
  const {register, handleSubmit, watch, formState:{errors}, } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },  
  });

  // Watch the form values (optional, for debugging)
  const email = watch("email");

  const onSubmit = (data: LoginFormData) => {
    console.log("Form submitted with data:", data);
    // Handle form submission logic here, e.g., API call to log in the user
  };

  const onError = (error: any) => {
    console.error("Form submission error:", error);

    // Handle form submission error here, e.g., show error message to the user
  };

  useEffect(() => {
    console.log("Email:", email);
    // console.log("Password:", password);
  }, [email]);

  return (
    <form onSubmit={handleSubmit(onSubmit, onError)} className="flex flex-col gap-4">
      <AuthInput type="email" placeholder="email" {...register("email")} error={errors.email?.message} />
      <AuthInput type="password" placeholder="password" {...register("password")} error={errors.password?.message} />
      <button
        type="submit"
        className="bg-emerald-500 rounded-full w-fit px-10 py-3 ml-auto text-white font-medium cursor-pointer" 
      >
        login
      </button>
      <hr className="border-gray-300" />
      <button type="button" className="bg-gray-100 shadow-md rounded-full py-3 px-6 w-full flex items-center cursor-pointer gap-6">
        <span><FaGoogle /></span>
        <span className="font-medium text-sm">Continue with Google</span>
      </button>
    </form>
  );
}