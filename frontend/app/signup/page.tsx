import SignupFormContainer from "@/components/Auth/SignupFormContainer";
import Navbar from "@/components/ui/Navbar";
import UnprotectedPageWrapper from "@/components/ui/UnprotectedPageWrapper";

export default function AuthPage() {
  return (
    <UnprotectedPageWrapper>
      <div className="flex flex-col grow py-16 items-center justify-center ">
        <SignupFormContainer />
      </div>
    </UnprotectedPageWrapper>
  )
}