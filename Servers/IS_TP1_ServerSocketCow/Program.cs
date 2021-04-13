using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Xml.Serialization;
using System.Linq;
using System.Collections.Generic;

namespace IS_TP1_ServerSocketCow
{
    class Program
    {
        private static int portServer = 4444;

        private static double euclidianDistance(tPosition a, tPosition b)
        {
            return Math.Sqrt(Math.Pow(a.xx - b.xx, 2) + Math.Pow(a.yy - b.yy, 2));
        }

        private static tPosition randomTPositionFromList(List<tPosition> positions)
        {
            return positions[new Random().Next(positions.Count)];
        }

        private static tMyPlace updateCowPosition(tMyPlace currentMyPlace)
        {
            tMyPlace nextMyPlace = currentMyPlace;

            List<tPlace> places = currentMyPlace.Place.ToList();
            tPlace currentPlace = places[0];
            List<tPlace> neighbours = places.Where((value, index) => index != 0).ToList();

            List<tPosition> wolvesPositions = neighbours.Where(neighbour => neighbour.Wolf).Select(wolf => wolf.Position).ToList();
            List<tPosition> validPositions = neighbours.Where(neighbour => neighbour.Position != null && !neighbour.Obstacle && neighbour.Grass > 0 && !neighbour.Wolf && !neighbour.Cow)
                .Select(validNeighbour => validNeighbour.Position).ToList();

            if (currentPlace.Grass > 0)
                validPositions.Add(currentPlace.Position);

            if (validPositions.Count > 0)
            {
                if (wolvesPositions.Count > 0)
                {
                    double maxDistance = 0;
                    tPosition maxDistancePosition = null;
                    foreach(tPosition validPosition in validPositions)
                    {
                        double distance = 0;
                        foreach(tPosition wolfPosition in wolvesPositions)
                            distance += euclidianDistance(validPosition, wolfPosition);

                        double averageDistance = distance / wolvesPositions.Count;
                        if (averageDistance > maxDistance)
                        {
                            maxDistance = averageDistance;
                            maxDistancePosition = validPosition;
                        }
                    }

                    nextMyPlace.Place[0].Position = maxDistancePosition;
                } 
                else
                {
                    nextMyPlace.Place[0].Position = randomTPositionFromList(validPositions);
                }
            }
            

            return nextMyPlace;
        }

        private static void StartListening()
        {
            // Data buffers
            byte[] bytes = new Byte[1024];

            XmlSerializer serializer = new XmlSerializer(typeof(tMyPlace));

            // Local endpoint for socket
            IPAddress ipAddress = IPAddress.Parse("127.0.0.1");
            IPEndPoint localEndPoint = new IPEndPoint(ipAddress, portServer);

            // TCP socket
            Socket socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                // Bind socket to local endpoint
                socket.Bind(localEndPoint);

                // Enable listening for incoming connections
                socket.Listen(10);
                Console.WriteLine("Server open on port 4444");

                Console.WriteLine("Waiting for connection");
                // Blocks while waiting for connection
                Socket handler = socket.Accept();

                // Process incoming connection
                Console.WriteLine("Connection established");

                // For each incoming connection
                while (true)
                {
                    string data = null;
                    while (true)
                    {
                        int bytesRec = handler.Receive(bytes);
                        data += Encoding.ASCII.GetString(bytes, 0, bytesRec);
                        if (data.IndexOf("</MyPlace>") > -1)
                            break;
                    }

                    Console.WriteLine("Received: {0}", data);

                    tMyPlace currentMyPlace = (tMyPlace)serializer.Deserialize(new MemoryStream(Encoding.ASCII.GetBytes(data)));
                    tMyPlace nextMyPlace = updateCowPosition(currentMyPlace);

                    handler.Send(Encoding.ASCII.GetBytes("Entao bom dia\nseus conas\nde sabao"));

                    string send = "";
                    using (MemoryStream stream = new MemoryStream())
                    {
                        serializer.Serialize(stream, nextMyPlace);
                        send = Encoding.ASCII.GetString(stream.ToArray());
                    }

                    Console.WriteLine("Sending: " + send);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        static void Main(string[] args)
        {
            StartListening();
        }
    }
}
