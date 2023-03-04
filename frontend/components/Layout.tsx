import { useState, ChangeEvent, MouseEvent } from "react";

import Container from "@mui/material/Container";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import AccountCircle from "@mui/icons-material/AccountCircle";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";

const Navbar = () => {
  const [auth, setAuth] = useState(true);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleMenu = (event: MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Libruch
          </Typography>
          <Button color="inherit">Przeglądaj książki</Button>
          <Button color="inherit">Przeglądaj organizacje</Button>
          {(auth && (
            <div>
              <IconButton
                size="large"
                aria-label="account of current user"
                aria-controls="menu-appbar"
                aria-haspopup="true"
                onClick={handleMenu}
                color="inherit"
              >
                <AccountCircle />
              </IconButton>
              <Menu
                id="menu-appbar"
                anchorEl={anchorEl}
                anchorOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
                keepMounted
                transformOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
                open={Boolean(anchorEl)}
                onClose={handleClose}
              >
                <MenuItem>Moje konto</MenuItem>
                <MenuItem
                  onClick={() => {
                    setAuth(false);
                    handleClose();
                  }}
                >
                  Wyloguj
                </MenuItem>
              </Menu>
            </div>
          ))}
        </Toolbar>
      </AppBar>
    </Box>
  );
};

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
        <Container maxWidth="lg">{children}</Container>
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
        <Container maxWidth="lg">{children}</Container>
        <Footer />
      </Grid>
    </>
  );
};
