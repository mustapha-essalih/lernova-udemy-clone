"use client";

import axios, { AxiosError } from "axios";
import {
	createContext,
	useContext,
	useState,
	useCallback,
	useMemo,
} from "react";
import Cookie from "universal-cookie";

interface UserProfile {
	id: string;
	username: string;
	firstname: string;
	lastname: string;
	email: string;
}

interface LoginResponse extends UserProfile {
	jwt: string;
}

type AuthContextType = {
	user: UserProfile | null;
	login: (username: string, password: string) => Promise<void>;
	logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
	user: null,
	login: async () => {},
	logout: () => {},
});

export const AuthContextProvider = ({
	children,
}: {
	children: React.ReactNode;
}) => {
	const [user, setUser] = useState<UserProfile | null>(null);
	const cookies = useMemo(() => new Cookie(), []);
	const expires = useMemo(() => new Date(), []);

	const login = useCallback(
		async (username: string, password: string) => {
			console.log({
				username,
				password,
			});
			try {
				const response = await axios.post(
					"http://localhost:8080/api/v1/auth/login",
					{
						username,
						password,
					}, {
						withCredentials: true
					}
				);
				const data = response.data as LoginResponse;
				const { jwt, ...profile } = data;
				setUser(profile);
				axios.defaults.headers.common["Authorization"] = `Bearer ${jwt}`;
				expires.setHours(expires.getHours() + 10);
				cookies.set("access_token", jwt, {
					expires,
				});
			} catch (e) {
				if (e instanceof AxiosError) {
					if (e.status === 401) throw new Error("Invalid username of password");
					else throw new Error("Something went wrong");
				}
			}
		},
		[cookies, expires]
	);

	const logout = useCallback(() => {
		setUser(null);
		delete axios.defaults.headers.common["Authorization"];
		cookies.remove("access_token");
	}, [setUser, cookies]);

	return <AuthContext value={{ user, login, logout }}>{children}</AuthContext>;
};

export const useAuth = () => {
	const context = useContext(AuthContext);

	return context;
};
