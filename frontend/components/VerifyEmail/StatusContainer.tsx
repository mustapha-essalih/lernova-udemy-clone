export function StatusContainer({children}: {children: React.ReactNode}) {
  return (
    <div className="bg-white rounded-xl pb-8 pt-16 shadow-md relative w-full max-w-[900px] flex flex-col items-center gap-6">
      {children}
    </div>
  );
}