import { useState, MouseEvent } from "react";

import Link from "next/link";
import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import AccountCircle from "@mui/icons-material/AccountCircle";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";

import ThemeToggler from "./ThemeToggler";

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
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            <Link href="/">Libruch</Link>
          </Typography>
          <Button color="inherit">
            <Link href="/books">Przeglądaj książki</Link>
          </Button>
          <Button color="inherit">
            <Link href="/organisations">Przeglądaj organizacje</Link>
          </Button>
          {auth && (
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
          )}
          <ThemeToggler />
        </Toolbar>
      </AppBar>
    </Box>
  );
};

export default Navbar;
