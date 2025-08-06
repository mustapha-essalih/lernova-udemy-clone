'use client';

import React, {MouseEventHandler, useState} from "react";
import Link from "next/link";
import SignupForm from "./SignupForm";

export default function SignupFormContainer() {
  const [role, setRole] = useState<'instructors' | 'students'>('students');

  const handleRoleChange: MouseEventHandler<HTMLButtonElement> = () => {
    setRole((prev) => prev === 'students' ? 'instructors' : 'students');
  };

  return (
    <div className={`xl:w-[80%] md:w-[95%] max-w-[1400px] relative min-h-[70vh] flex items-stretch justify-between`}>
      <div className={`lg:block hidden rounded-4xl w-[58%] transition-all relative ease-out duration-300 ${role === 'students' ? 'translate-x-0' : 'translate-x-[calc(72.42%)]'} `} style={{backgroundImage: `url('/images/${role === 'students' ? 'teacher' : 'student'}.jpg')`, backgroundSize: "cover", backgroundPosition: 'center'}}>
        <button onClick={handleRoleChange} className={`bg-white text-black font-medium h-[72px] w-[182px] rounded-full ease-out duration-300 absolute top-5 ${role === 'students' ? 'left-5' : 'left-[calc(100%-202px)]' } cursor-pointer`}>Sign up as <span className="capitalize">{role === 'students' ? 'teacher' : 'student'}</span></button>
      </div>
      <div className={`flex flex-col items-center bg-white py-12 px-4 lg:px-8 xl:px-12 rounded-4xl w-full lg:w-[38%] shadow-md transition-all ease-out duration-300 ${role === 'students' ? 'translate-x-0' : '-translate-x-[163.16%]'}`} >
        <h1 className="text-4xl font-bitcount font-medium">Sign up</h1>
        <SignupForm role={role} />
        <hr className="border-[#BDBDBD] w-full my-8" />
        <button className="" type="button">
          <span className="text-gray-500">Already have an account? </span>
          <Link href="login" className="text-emerald-500 font-medium">Sign in</Link>
        </button>
      </div>
    </div>
  );
}