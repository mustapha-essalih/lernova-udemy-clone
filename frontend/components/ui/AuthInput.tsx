
import { twMerge } from "tailwind-merge";

interface AuthInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  error?: string;
}

export default function AuthInput({className, ...props}: AuthInputProps) {
  return (
    <div>
      <input
          {...props}
          className={twMerge("bg-gray-100 shadow-md rounded-full py-3 px-6 w-full placeholder:capitalize outline-none", props.className)}
        />
      {
        props.error && <p className="text-red-500 text-sm mt-1">{props.error}</p>
      }
    </div>
  );
}