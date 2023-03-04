import Head from "next/head";

import { UnauthorizedLayout } from "../components/Layout";
import { Button } from "@mui/material";

export default function Home({ children }) {
  return (
    <>
      <Head>
        <title>Libruch</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <UnauthorizedLayout>
        <h1> hej wypożyczarka książek</h1>
        <Button>Zaloguj się z googlem</Button>

        {children}
      </UnauthorizedLayout>
    </>
  );
}
