import { useRouter } from "next/router";
import React, { createContext, useContext, useEffect, useState } from "react";
interface User {
  subject: string;
}

interface AuthContextData {
  user: User;
  loading: boolean;
}
export const AuthContext = createContext<AuthContextData | null>(null);

export const AuthProvider = ({ children }) => {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetch("/auth/user")
      .then((res) => {
        if (res.ok) {
          return res.json();
        }
        if (res.status === 401) {
          return null;
        }
        throw new Error("Failed to get auth data");
      })
      .then((user) => {
        if (!user) {
          router.push({
            pathname: "/login",
          });
          return;
        }
        setUser(user);
        setLoading(false);
      });
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export function useAuth() {
  const contextData = useContext(AuthContext);

  if (!contextData) {
    throw new Error("Authentication context not available");
  }

  return contextData;
}
