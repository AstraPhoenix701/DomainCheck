import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Comparator;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class App {

    public static Date checkDomain(String host) throws IOException {
        return checkDomain(host, 443);
    }

    public static Date checkDomain(String host, Integer port) throws IOException {

        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslsocket = (SSLSocket) sslsocketfactory
                .createSocket();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        sslsocket.connect(socketAddress, 1000);

        Certificate[] peerCertificates = sslsocket.getSession().getPeerCertificates();
        X509Certificate certificate = (X509Certificate) peerCertificates[0];
        // System.out.println("connection opened");

        Date result = certificate.getNotAfter();

        sslsocket.close();
        // System.out.println("connection closed");

        return result;
    }

    public static long timeLeft(Date fdate) {
        Date pdate = new Date();
        long result = Math.abs(pdate.getTime() - fdate.getTime());
        return result;
    }

    public static long milliToDays(long time) {
        time /= 1000;
        time /= 60;
        time /= 60;
        time /= 24;
        return time;
    }

    public static List<String> readFile(String fileName)
            throws IOException {

        List<String> result = new ArrayList<>();
        int counter = 0;
        String file = fileName;

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String currentLine = reader.readLine();
        while (currentLine != null) {

            // System.out.println(counter);
            if (counter >= 0) {
                // System.out.println(currentLine);
                result.add(currentLine);
            }
            counter++;
            currentLine = reader.readLine();

        }
        reader.close();

        return result;
    }

    public static List<Domain> checkAll(List<String> hostnames) throws IOException {

        List<Domain> domains = new ArrayList<>();

        for (String i : hostnames) {
            // System.out.println(i);
            try {
                Date date = checkDomain(i);
                Domain domain = new Domain(i, date, true);
                domains.add(domain);
            } catch (Exception e) {
                Domain domain = new Domain(i, null, false);
                domains.add(domain);
            }

        }

        return domains;
    }

    public static void sortAll(List<Domain> domains) {

        domains.sort(new Comparator<Domain>() {
            @Override
            public int compare(App.Domain k1, App.Domain k2) {
                if (k1.notAfter == null) {
                    if (k2.notAfter == null) {
                        return 0;
                    }
                    return 1;
                }
                if (k2.notAfter == null) {
                    return -1;
                }

                return k1.notAfter().compareTo(k2.notAfter()) * -1;
            }
        });
    }

    private static void printResult(List<App.Domain> allDomains) {
        for (Domain d : allDomains) {
            if (d.notAfter() != null && milliToDays(timeLeft(d.notAfter)) > 21) {
                d.print();
            }
        }
        System.out.println("----------------------------------------------------------------------------------\n");
        for (Domain d : allDomains) {
            if (d.notAfter() != null && milliToDays(timeLeft(d.notAfter)) <= 21) {
                d.print();
            }
        }
        System.out.println("----------------------------------------------------------------------------------\n");
        for (Domain d : allDomains) {
            if (d.notAfter() == null) {
                System.out.println(d.hostname() + " | Fehler beim Überprüfen des Zertifikats\n");
            }
        }
    }

    public record Domain(String hostname, Date notAfter, boolean success) {

        public void print() {
            System.out
                    .println(String.format("%s | %s | %s\n", hostname(), milliToDays(timeLeft(notAfter)), notAfter));
        }

        public List<String> toTableRow() {
            return List.of(hostname(), notAfter == null ? "N/A" : String.valueOf(milliToDays(timeLeft(notAfter))),
                    notAfter == null ? "N/A" : notAfter.toString());
        }
    }

    public static List<Integer> space(List<Domain> allDomains) {

        List<Integer> result = new ArrayList<>();
        int counter = 0;
        boolean lock = false;
        boolean lock2 = false;
        // result.add(0);
        // result.add(0);

        for (Domain d : allDomains) {
            if (d.notAfter == null) {
                if (lock2 == false) {
                    result.add(counter);
                    lock2 = true;
                }
            } else if (milliToDays(timeLeft(d.notAfter)) <= 21) {
                if (lock == false) {
                    result.add(counter);
                    lock = true;
                }
            }
            counter++;
        }
        if (result.size() < 1) {
            result.add(counter);
            result.add(counter);
        }
        if (result.size() < 2) {
            result.add(counter);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {

        System.err.println("Process started, please wait for completion...\n");
        List<String> file = readFile("domains.txt");
        List<Domain> allDomains = checkAll(file);
        sortAll(allDomains);
        Table table = new Table();
        table.addTitle(List.of("Hostname", "Remaining Days", "Expiration Date"));
        for (Domain d : allDomains) {
            table.addRow(d.toTableRow());
        }
        table.printTo(space(allDomains).get(0));
        table.printFromTo(space(allDomains).get(0), space(allDomains).get(1));
        table.printFrom(space(allDomains).get(1));

    }
}
