using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Xml.Serialization;
using System.Linq;
using System.Collections.Generic;
using System.Xml;

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

            List<tPosition> wolvesPositions = neighbours.Where(neighbour => neighbour.Wolf)
                .Select(wolf => wolf.Position).ToList();
            List<tPosition> validPositions = neighbours
                .Where(neighbour => neighbour.Position != null && !neighbour.Obstacle
                    && neighbour.Grass > 0 && !neighbour.Wolf && !neighbour.Cow)
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

        private static string socketStreamToString(Socket handler, XmlSerializer deserializer)
        {
            byte[] bytes = new Byte[1024];
            string data = null;

            while (true)
            {
                int bytesRec = handler.Receive(bytes);
                data += Encoding.ASCII.GetString(bytes, 0, bytesRec);
                if (data.IndexOf("</MyPlace>") > -1)
                    break;
            }

            return data;
        }

        private static string serializeTMyPlaceToString(tMyPlace myPlace, XmlSerializer serializer)
        {
            string xml = "";

            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = false;

            using (MemoryStream stream = new MemoryStream())
                using (XmlWriter writer = XmlWriter.Create(stream, settings))
                {
                    serializer.Serialize(writer, myPlace);
                    xml = Encoding.ASCII.GetString(stream.ToArray());
                }

            // XmlWriter generates ??? in the beginning
            return xml.Substring(3) + '\n';
        }

        private static void StartListening()
        {
            // Local endpoint for TCP socket
            IPAddress ipAddress = IPAddress.Parse("127.0.0.1");
            IPEndPoint localEndPoint = new IPEndPoint(ipAddress, portServer);
            Socket listener = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            // (De)serializer
            XmlSerializer serializer = new XmlSerializer(typeof(tMyPlace));

            try
            {
                // Bind socket to local endpoint and start listenning
                listener.Bind(localEndPoint);
                listener.Listen(10);

                // Block waiting for connection
                Socket handler = listener.Accept();

                while (true)
                {
                    string received = socketStreamToString(handler, serializer);
                    tMyPlace currentMyPlace = (tMyPlace) serializer
                        .Deserialize(new MemoryStream(Encoding.ASCII.GetBytes(received)));

                    tMyPlace nextMyPlace = updateCowPosition(currentMyPlace);

                    string serialized = serializeTMyPlaceToString(nextMyPlace, serializer);
                    handler.Send(Encoding.ASCII.GetBytes(serialized));
                    //handler.Shutdown(SocketShutdown.Both);
                    //handler.Close();
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
