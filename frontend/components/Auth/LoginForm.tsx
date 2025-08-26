'use client';

import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { FaGoogle } from "react-icons/fa";
import { useForm } from "react-hook-form";

import AuthInput from "../ui/AuthInput";
import { useAuth } from "../context/AuthContext";
import ErrorMessage from "../ui/ErrorMessage";
import { CircularProgress } from "@mui/material";

const loginSchema = z.object({
  username: z.string().min(1, "Username is required"),
  password: z.string().min(8, "Password must be at least 8 characters long"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginForm() {
  const { login } = useAuth();

  const {register, handleSubmit, setError, formState:{errors, isSubmitting}, } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      username: "",
      password: "",
    },  
  });
  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data.username, data.password);
    } catch(e) {
      if (e instanceof Error) setError('root', {message: e.message})
    }
    // Handle form submission logic here, e.g., API call to log in the user
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
      <AuthInput type="text" placeholder="username" {...register("username")} error={errors.username?.message} />
      <AuthInput type="password" placeholder="password" {...register("password")} error={errors.password?.message} />
      {
        errors.root?.message && <ErrorMessage content={errors.root?.message} />
      }
      <button
        type="submit"
        disabled={isSubmitting}
        className={`bg-emerald-500 rounded-full h-[48px] w-[115px] ml-auto flex items-center justify-center text-white font-medium ${isSubmitting ? 'cursor-not-allowed' : 'cursor-pointer' }`} 
      >
        {
          isSubmitting ? <CircularProgress size={22} thickness={5} color="inherit" />
           : "login"
        }
      </button>
      <hr className="border-gray-300" />
      <button type="button" className="bg-gray-100 shadow-md rounded-full py-3 px-6 w-full flex items-center cursor-pointer gap-6">
        <span><FaGoogle /></span>
        <span className="font-medium text-sm">Continue with Google</span>
      </button>
    </form>
  );
}