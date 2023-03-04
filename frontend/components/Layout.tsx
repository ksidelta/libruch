import { useState, ChangeEvent, MouseEvent } from "react";

import Container from "@mui/material/Container";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
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

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    setAuth(event.target.checked);
  };

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
          )) || (
            <Button onClick={() => setAuth(true)} color="inherit">
              Zaloguj się
            </Button>
          )}
        </Toolbar>
      </AppBar>
    </Box>
  );
};

export const UnauthorizedLayout = ({ children }) => {
  return (
    <>
      <Navbar />
      <Container maxWidth="lg">{children}</Container>
    </>
  );
};

export const AuthorizedLayout = ({ children }) => {
  return (
    <>
      <Navbar />
      <Container maxWidth="lg">{children}</Container>
    </>
  );
};
