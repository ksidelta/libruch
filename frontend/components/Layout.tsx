import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import CssBaseline from "@mui/material/CssBaseline";

import Navbar from "./Navbar";
import { ComponentType, PropsWithChildren } from "react";

const Footer = () => {
  return (
    <Box>
      <div style={{ textAlign: 'center', padding: '2rem' }}>Copyright (&copy;) <a href="https://hsp.sh">hsp.sh</a></div>
    </Box>
  );
};

type LayoutGridProps = PropsWithChildren<{
  Footer?: ComponentType;
  Navigation?: ComponentType;
}>
const LayoutGrid = ({ Navigation, children, Footer }: LayoutGridProps) => {
  return <div style={{
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh',
  }}>
    {Navigation && <Navigation />}

    <div style={{ flexGrow: 1 }}>
      {children}
    </div>

    {Footer && <Footer />}
  </div>
}

export const UnauthorizedLayout = ({ children }) => {
  return (
    <>
      <CssBaseline />
      <LayoutGrid
        Footer={Footer}
      >
        <Grid
          container
          direction="column"
          justifyContent="center"
          alignItems="center"
          height="100%"
        >
          {children}
        </Grid>
      </LayoutGrid>
    </>
  );
};

export const AuthorizedLayout = ({ children }) => {
  return (
    <>
      <CssBaseline />
      <LayoutGrid
        Navigation={Navbar}
        Footer={Footer}>
        <Grid
          container
          direction="column"
          justifyContent="center"
          alignItems="center"
          height="100%"
        >
          {children}
        </Grid>
      </LayoutGrid>
    </>
  );
};
