import Navbar from "./Navbar";

export default function UnprotectedPageWrapper({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<div className="flex flex-col min-h-screen bg-gray-100">
			<Navbar />
			{children}
		</div>
	);
}
