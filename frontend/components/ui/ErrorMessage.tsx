import { MdErrorOutline } from "react-icons/md";
import { twMerge } from "tailwind-merge";

export default function ErrorMessage({content, className}: {content: string, className?: string}) {
    return (
        <div className={twMerge("bg-red-500 flex items-center justify-center gap-3 px-5 py-2 text-white rounded-full w-fit", className)}>
            <MdErrorOutline size={22} />
            <p className="font-medium">{content}</p>
        </div>
    );
}