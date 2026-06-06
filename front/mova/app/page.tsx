<<<<<<< Updated upstream
import Header from "../components/layout/header";

export default function Home() {
  return (
    <main>
      <Header />
    </main>
  );
=======
// import { redirect } from "next/navigation";

import { AuthLoginPage } from "./auth/login/pageLogin";
/*
  export const metadata = {
  title: "Home",
};

export default function Home() {
  redirect("/feed");
}
*/
export default function Home() {
  return(
    <main>
      <AuthLoginPage />
    </main>
  )
>>>>>>> Stashed changes
}