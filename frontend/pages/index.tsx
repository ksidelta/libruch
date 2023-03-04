import Head from "next/head";
import Image from "next/image";
import Link from "next/link";

import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Grid from "@mui/material/Grid";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";

import { UnauthorizedLayout } from "../components/Layout";
import profilePic from "../public/hello.png";

export default function Home() {
  return (
    <>
      <Head>
        <title>Libruch</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <UnauthorizedLayout>
        <Grid
          container
          spacing={0}
          direction="column"
          alignItems="center"
          justifyContent="center"
          sx={{ minHeight: "100vh" }}
        >
          <Card>
            <CardContent sx={{ textAlign: "center" }}>
              <Image src={profilePic} alt="Kitty" />
              <h2 style={{ maxWidth: "70%", margin: "auto" }}>
                Książki mamy, wypożyczenia mamy, inni też mają książki, które
                możesz im zabrać lub dać swoje.
              </h2>

              <Box sx={{ m: 4 }}>
                <Button variant="contained" size="large">
                  <Link href="/">Zaloguj się z googlem</Link>
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </UnauthorizedLayout>
    </>
  );
}
