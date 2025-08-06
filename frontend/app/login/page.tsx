'use client';

import LoginForm from "@/components/Auth/LoginForm";
import UnprotectedPageWrapper from "@/components/ui/UnprotectedPageWrapper";

export default function LoginPage() {
  return (
    <UnprotectedPageWrapper>
      <div className="flex flex-col grow items-center justify-center">
        <div className="h-[80vh] xl:w-[80%] w-[95%] relative rounded-4xl lg:px-6 lg:py-6 md:p-12 p-0 flex bg-[url('/images/female-student-login-2.jpg')] bg-cover bg-center ">
          <div className=" w-full lg:w-1/3 lg:min-w-[500px] lg:max-w-[900px] rounded-2xl md:backdrop-blur-lg bg-white md:bg-transparent shadow-md md:shadow-none md:border md:border-gray-600 ml-auto p-5 flex flex-col justify-center">
            <h2 className="text-2xl font-medium text-center mb-5 md:text-white">Welcome Back to Learnova</h2>
            <p className="text-center md:text-white">With Learnova, every login is a step closer to your goals. Pick up where you left off and keep growing.</p>
            <div className="w-full px-6 py-12 mt-12 rounded-2xl">
              <LoginForm />
            </div>
          </div>
        </div>
      </div>
    </UnprotectedPageWrapper>
  );
}
/**
 * 
 * M0,0.3 Q0,0 0.3,0
M 0,r Q 0,0 r,0

 */