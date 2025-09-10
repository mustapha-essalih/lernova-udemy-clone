'use client';


import { createPortal } from "react-dom";

export default function Notification() {
		return createPortal(
			<div
				className="fixed top-24 left-1/2 -translate-x-1/2 z-50
                animate-[slide-down_0.5s_cubic-bezier(0.4,0,0.2,1)]
            "
				style={{
					// fallback for environments that don't support arbitrary keyframes
					animation: "slide-down 0.5s cubic-bezier(0.4,0,0.2,1)",
				}}
			>
				<div className="bg-emerald-500 text-white px-6 font-medium py-3 text-sm rounded-lg shadow-lg">
					Email verification link has been sent to your email
				</div>
			</div>,
			document.body
		);
	};