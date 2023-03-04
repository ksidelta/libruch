import Head from "next/head";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import "../styles/globals.css";

import { UnauthorizedLayout } from "../components/Layout";
import { Button } from "@mui/material";

export default function Home() {
  return (
    <>
      <Head>
        <title>Libruch</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <UnauthorizedLayout>
        <h1> hej wypożyczarka książek</h1>
        <Button>Zaloguj się z googlem</Button>
      </UnauthorizedLayout>
    </>
  );
}
