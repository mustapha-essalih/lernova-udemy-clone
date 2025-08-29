'use client';

import { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import axios from "axios";
import { useRouter } from "next/navigation";


import UnprotectedPageWrapper from "@/components/ui/UnprotectedPageWrapper";
import { Success } from "@/components/VerifyEmail/Success";
import { Failed } from "@/components/VerifyEmail/Failed";
import { Expired } from "@/components/VerifyEmail/Expired";
import { Loading } from "@/components/VerifyEmail/Loading";

export default function VerifyPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [loading, setLoading] = useState(true);
  const [successMsg, setSuccessMsg] = useState<string>("Your email has been verified successfully");
  const [verificationStatus, setVerificationStatus] = useState<'success' | 'failed' | 'expired' | null>(null);
  const token = searchParams.get("token");

  const verifyEmail = async (token: string) => {
    try {
      await axios.get(`http://localhost:8081/api/v1/auth/email-verification?token=${token}`);

      setVerificationStatus('success');
    } catch (e) {
      if (axios.isAxiosError(e) && e.response) {
        if (e.status === 400) {
          if (e.response.data.includes("verified")) {
            setVerificationStatus('success');
            setSuccessMsg("Your account has already been verified, please, login.");
          }
          else if (e.response.data.includes("expired")) {
            setVerificationStatus('expired');
          }
          else setVerificationStatus('failed');
            
        } else {
          setVerificationStatus('failed');
        }
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (token)
      verifyEmail(token);
    else
      router.replace('/login');
  }, [token]);

  return (
    <UnprotectedPageWrapper>
      <div className="flex items-center justify-center grow">
        {
          !loading && verificationStatus === 'success' && <Success msg={successMsg} />
        }
        {
          !loading && verificationStatus === 'failed' && <Failed />
        }
        {
          !loading && verificationStatus === 'expired' && <Expired />
        }
        {
          loading && <Loading />
        }
      </div>
    </UnprotectedPageWrapper>
  );
}