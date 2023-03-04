import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import CssBaseline from "@mui/material/CssBaseline";

import Navbar from "./Navbar";

const Footer = () => {
  return (
    <Box>
      Copyright (ć) <a href="https://hsp.sh">hsp.sh</a>
    </Box>
  );
};

export const UnauthorizedLayout = ({ children }) => {
  return (
    <>
      <CssBaseline />
      <Grid
        container
        direction="column"
        justifyContent="center"
        alignItems="center"
      >
        {children}
        <Footer />
      </Grid>
    </>
  );
};

export const AuthorizedLayout = ({ children }) => {
  return (
    <>
      <CssBaseline />
      <Navbar />
      <Grid
        container
        direction="column"
        justifyContent="center"
        alignItems="center"
      >
        {children}
        <Footer />
      </Grid>
    </>
  );
};
