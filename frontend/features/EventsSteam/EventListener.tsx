import {
  createContext,
  useContext,
  useState,
  PropsWithChildren,
  useEffect,
} from "react";

interface EventListenerContextData {
  data: unknown;
  listener: EventSource;
}

type EventListenerProps = PropsWithChildren<{
  url: string;
}>;

export const EventListenerContext =
  createContext<EventListenerContextData | null>(null);

export const EventListenerProvider = ({
  url,
  children,
}: EventListenerProps) => {
  const [data, setData] = useState<unknown>(null);
  const [listener, setListener] = useState<EventSource | null>(null);

  useEffect(() => {
    const list = new EventSource(url);
    list.onmessage = ({ data }) => {
      setData(data);
    };
    setListener(list);
  }, []);

  return (
    <EventListenerContext.Provider value={{ data, listener }}>
      {children}
    </EventListenerContext.Provider>
  );
};

export const useEventListener = () => {
  const contextData = useContext(EventListenerContext);

  if (!contextData) {
    throw new Error("bruh");
  }

  return contextData;
};
