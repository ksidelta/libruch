import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";

import Navbar from "./Navbar"

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
