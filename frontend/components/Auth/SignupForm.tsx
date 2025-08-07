"use client";

import z from "zod";
import AuthInput from "../ui/AuthInput";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { useRouter } from "next/navigation";
import { Alert, CircularProgress } from "@mui/material";
import Snackbar, { SnackbarCloseReason } from '@mui/material/Snackbar';
import { useState } from "react";

const signupSchema = z
	.object({
		firstName: z.string().min(1, "First name is required"),
		lastName: z.string().min(1, "Last name is required"),
		username: z.string().min(1, "Username is required"),
		email: z.email("Invalid email address"),
		password: z.string().min(8, "Password must be at least 8 characters long"),
		confirmPassword: z
			.string()
			.min(8, "Confirm password must be at least 8 characters long"),
	})
	.refine((data) => data.password === data.confirmPassword, {
		message: "Passwords do not match",
    path: ["confirmPassword"],
	});

type SignupFormData = z.infer<typeof signupSchema>;


export default function SignupForm({role}: {role: 'students' | 'instructors'}) {
	const router = useRouter();
	const [successLogin, setSuccessLogin] = useState(false);
	const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm({
		resolver: zodResolver(signupSchema),
		defaultValues: {
			firstName: "",
			lastName: "",
			username: "",
			email: "",
			password: "",
			confirmPassword: ""
		}
  });
	
  const onSubmit = async (data: SignupFormData) => {
		try {
			const response = await axios.post(`http://localhost:8080/api/v1/auth/${role}`, data); // use localhost not 127.0.0.1

			setSuccessLogin(false);
			setTimeout(() => {
				router.push("/login")
			}, 5000)			
		} catch(e) {
			console.log(e)
		}


  }

	return (
		<form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-6 mt-8 w-full">
      <AuthInput type="text" placeholder="First Name" {...register('firstName')} error={errors.firstName?.message} />
      <AuthInput type="text" placeholder="Last Name" {...register("lastName")} error={errors.lastName?.message} />
      <AuthInput type="text" placeholder="Username" {...register("username")} error={errors.username?.message} />
      <AuthInput type="email" placeholder="email" {...register('email')} error={errors.email?.message} />
      <AuthInput type="password" placeholder="password" {...register('password')} error={errors.password?.message} />
      <AuthInput type="password" placeholder="confirm password" {...register('confirmPassword')} error={errors.confirmPassword?.message} />
			<button
				type="submit"
				className={`bg-emerald-500 rounded-full w-[133px] h-[50px] py-3 ml-auto text-white font-medium flex items-center justify-center ${isSubmitting ? "cursor-not-allowed" : "cursor-pointer"}`}
				disabled={isSubmitting}
			>
				{isSubmitting ? <CircularProgress size={22} thickness={5} color="inherit" /> : "Sign up"}
			</button>
			<Snackbar open={successLogin} autoHideDuration={6000} onClose={() => setSuccessLogin(false)}>
				<Alert
					onClose={() => setSuccessLogin(false)}
					severity="success"
					variant="filled"
					sx={{ width: '100%' }}
				>
					You Have Successfuly Signed Up
				</Alert>
			</Snackbar>
		</form>
	);
}
