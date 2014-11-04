package analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PacketAnalyzer {

	public static void main(String[] args) {
		System.out
				.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Analyis of IEEE 802.11 packets<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out
				.println("-----------------------------------------------------------------------------------------------------");
		Magic magic = new Magic();

		String source = "sample.txt";   //convert pcap file into txt

		System.out.println("No. of APs in network : " + magic.findAPs(source));

		System.out.println("No. of Clients in network : "
				+ magic.findTotalClients(source));

		System.out.println("AP with max bits : "
				+ magic.findApsWithMaxBits(source)); // call findAPs before

		magic.findBitRates(source);

		System.out.println("Fraction of Data frames Retransmitted: "
				+ magic.findRetransmitted() * 100 + " %");// call //
															// findapswithmaxbits
		// before this

		System.out.println("Fraction of Data Frames use RTS/CTS: "
				+ magic.findfractionRC() * 100 + " %");
		System.out.println("Client(s) sent maximum request :"
				+ magic.clientsWithMax);

		magic.findTotalUpDown();

		System.out.println("Fraction of Overhead is : "
				+ magic.findfractionOverhead() * 100 + " %");

		System.out
				.println("-----------------------------------------------------------------------------------------------------");

	}
}

class Magic {

	ArrayList<String> aps = new ArrayList<String>();
	ArrayList<String> clients = new ArrayList<String>();
	ArrayList<String> retrans = new ArrayList<String>();
	ArrayList<String> drc = new ArrayList<String>();
	ArrayList<String> clientsWithMax = new ArrayList<String>();
	ArrayList<String> overhead = new ArrayList<String>();
	ArrayList<String> updown = new ArrayList<String>();

	public int findAPs(String source) {
		aps.clear();
		String splitBy = "\\(";
		String line = null;
		BufferedReader br = null;

		Pattern aps_pattern = Pattern.compile("\\sBSS\\sId:\\s");

		try {
			br = new BufferedReader(new FileReader(source));

			while ((line = br.readLine()) != null) {

				Matcher aps_matcher = aps_pattern.matcher(line);

				if (aps_matcher.find()) {
					String[] b = line.split(splitBy);
					String apid = b[1].substring(0, b[1].length() - 1);
					if (!aps.contains(apid))
						aps.add(apid);
				}
			}
			// System.out.println(aps); //uncomment to print aps
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return aps.size();
	}

	public String findApsWithMaxBits(String source) {
		retrans.clear();
		drc.clear();
		String splitBy = "\\(";
		String line = null;
		BufferedReader br = null;

		Pattern frame_len_pattern = Pattern.compile("\\sFrame\\sLength:\\s");
		Pattern typesub_pattern = Pattern.compile("\\sType/Subtype:\\s");
		Pattern frame_type_pattern = Pattern
				.compile(".\\s=\\sType:\\sData\\sframe");
		Pattern retry_pattern = Pattern.compile(".\\s=\\sRetry:\\s");
		Pattern bss_pattern = Pattern.compile("\\sBSS\\sId:\\s");
		Pattern frame_pattern = Pattern.compile("Frame\\s\\d+:\\s");
		ArrayList<String> aps = new ArrayList<String>();
		ArrayList<Long> aps_size = new ArrayList<Long>();
		ArrayList<String> frames = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(source));

			while ((line = br.readLine()) != null) {
				Matcher frame_matcher = frame_pattern.matcher(line);
				Matcher typesub_matcher = typesub_pattern.matcher(line);
				Matcher frame_len_matcher = frame_len_pattern.matcher(line);
				Matcher frame_type_matcher = frame_type_pattern.matcher(line);
				Matcher bss_matcher = bss_pattern.matcher(line);
				Matcher retry_matcher = retry_pattern.matcher(line);
				if (retry_matcher.find())
					retrans.add(line);
				if (typesub_matcher.find()) {
					if (line.contains("Request-to-send")) {
						drc.add(line);
					} else if (line.contains("Clear-to-send")) {
						drc.add(line);
					}
				}
				if (frame_matcher.find())
					frames.add(line);
				else if (frame_len_matcher.find())
					frames.add(line);
				else if (frame_type_matcher.find()) {
					frames.add(line);
					retrans.add(line);
				} else if (bss_matcher.find())
					frames.add(line);

			}

			for (int i = 0; i < frames.size(); i++) {

				String nline = frames.get(i);

				if (nline.contains("Data frame")) {
					String lengthline = frames.get(i - 1);
					String bssline = frames.get(i + 1);

					String[] b = lengthline.split(splitBy);
					long size = Integer.parseInt(b[1].substring(0,
							b[1].length() - 6));

					String[] bb = bssline.split(splitBy);
					String bss = bb[1].substring(0, bb[1].length() - 1);

					if (aps.contains(bss)) {
						long old_size = aps_size.get(aps.indexOf(bss));
						aps_size.set(aps.indexOf(bss), size + old_size);
					} else {
						aps.add(bss);
						aps_size.add(size);
					}
				}

			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		long max_value = Collections.max(aps_size);
		int index = aps_size.indexOf(max_value);
		// System.out.println(max_value);
		return aps.get(index);
	}

	public void findBitRates(String source) {

		String splitBy = "\\(";
		String line = null;
		BufferedReader br = null;

		Pattern frame_len_pattern = Pattern.compile("\\sFrame\\sLength:\\s");

		Pattern frame_type_pattern = Pattern.compile("\\sQoS\\sData,");
		Pattern duration_pattern = Pattern
				.compile("\\s=\\sDuration:\\s\\d+\\smicroseconds");
		Pattern bss_pattern = Pattern.compile("\\sBSS\\sId:\\s");
		Pattern frame_pattern = Pattern.compile("Frame\\s\\d+:\\s");

		ArrayList<String> frames = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(source));

			while ((line = br.readLine()) != null) {
				Matcher frame_matcher = frame_pattern.matcher(line);
				Matcher frame_len_matcher = frame_len_pattern.matcher(line);
				Matcher frame_type_matcher = frame_type_pattern.matcher(line);
				Matcher bss_matcher = bss_pattern.matcher(line);
				Matcher duration_matcher = duration_pattern.matcher(line);
				if (frame_matcher.find())
					frames.add(line);
				else if (frame_len_matcher.find())
					frames.add(line);
				else if (frame_type_matcher.find())
					frames.add(line);
				else if (bss_matcher.find())
					frames.add(line);
				else if (duration_matcher.find())
					frames.add(duration_matcher.group());
			}
			PrintWriter writer = new PrintWriter("bitrate.txt");
			
			for (int i = 0; i < frames.size(); i++) {
				// System.out.println(frames.get(i));
				String nline = frames.get(i);

				if (nline.contains("IEEE 802.11 QoS Data")) {
					String lengthline = frames.get(i - 1);
					String durationline = frames.get(i + 1);

					String[] b = lengthline.split(splitBy);
					long length = Integer.parseInt(b[1].substring(0,
							b[1].length() - 6));

					String[] bb = durationline.split(":");
					long duration = Long.parseLong(bb[1].substring(0,
							bb[1].length() - 12).trim());

					double bitpermicrosec = (double) length / duration;

					writer.println(bitpermicrosec);
				}

			}
			writer.close();
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public int findTotalClients(String source) {
		clients.clear();
		overhead.clear();
		String splitBy = "\\(";
		String line = null;
		BufferedReader br = null;
		Pattern frame_pattern = Pattern.compile("Frame\\s\\d+:\\s");
		Pattern typesub_pattern = Pattern.compile("\\sType/Subtype:\\s");
		Pattern frame_type_pattern = Pattern
				.compile(".\\s=\\sType:\\sData\\sframe");
		Pattern dssta_pattern = Pattern.compile("\\.10\\s=\\sDS"); // DS to STA
		Pattern stads_pattern = Pattern.compile("\\.01\\s=\\sDS"); // STA to DS
		Pattern ds00_pattern = Pattern.compile("\\.00\\s=\\sDS");
		Pattern ds11_pattern = Pattern.compile("\\.11\\s=\\sDS");
		Pattern dest_pattern = Pattern.compile("\\sDestination\\saddress:\\s");
		Pattern recv_pattern = Pattern.compile("\\sReceiver\\saddress:\\s");
		Pattern trans_pattern = Pattern.compile("\\sTransmitter\\saddress:\\s");
		Pattern source_pattern = Pattern.compile("\\sSource\\saddress:\\s");
		Pattern bss_pattern = Pattern.compile("\\sBSS\\sId:\\s");
		ArrayList<String> frames = new ArrayList<String>();
		ArrayList<String> apsunique = new ArrayList<String>();
		ArrayList<Long> aps_sizes = new ArrayList<Long>();

		try {
			br = new BufferedReader(new FileReader(source));

			while ((line = br.readLine()) != null) {
				Matcher frame_matcher = frame_pattern.matcher(line);
				Matcher typesub_matcher = typesub_pattern.matcher(line);
				Matcher dssta_matcher = dssta_pattern.matcher(line);
				Matcher stads_matcher = stads_pattern.matcher(line);
				Matcher ft_matcher = frame_type_pattern.matcher(line);
				Matcher dest_matcher = dest_pattern.matcher(line);
				Matcher recv_matcher = recv_pattern.matcher(line);
				Matcher trans_matcher = trans_pattern.matcher(line);
				Matcher source_matcher = source_pattern.matcher(line);
				Matcher bss_matcher = bss_pattern.matcher(line);
				Matcher ds00_matcher = ds00_pattern.matcher(line);
				Matcher ds11_matcher = ds11_pattern.matcher(line);
				if (frame_matcher.find())
					frames.add(line);
				else if (typesub_matcher.find()) {
					frames.add(line);
					overhead.add(line);
				} else if (ds00_matcher.find())
					frames.add(line);
				else if (ds11_matcher.find())
					frames.add(line);
				else if (dssta_matcher.find()) {
					updown.add(line);
					frames.add(line);
				} else if (stads_matcher.find()) {
					updown.add(line);
					frames.add(line);
				} else if (dest_matcher.find())
					frames.add(line);
				else if (recv_matcher.find())
					frames.add(line);
				else if (trans_matcher.find())
					frames.add(line);
				else if (source_matcher.find())
					frames.add(line);
				else if (ft_matcher.find()) {
					updown.add(line);
					frames.add(line);
				} else if (bss_matcher.find())
					frames.add(line);

			}

			for (int i = 0; i < frames.size(); i++) {

				// System.out.println(frames.get(i));
				String nline = frames.get(i);

				if (nline.contains("Data frame")) {

					String dsline = frames.get(i + 1);
					if (dsline.contains("10 = DS status")) {
						String destline = frames.get(i + 3);
						String[] b = destline.split(splitBy);
						String apid = b[1].substring(0, b[1].length() - 1);
						if (!clients.contains(apid))
							clients.add(apid);
					} else if (dsline.contains("01 = DS status")) {
						String sourceline = frames.get(i + 5);
						String[] b = sourceline.split(splitBy);
						String apid = b[1].substring(0, b[1].length() - 1);
						if (!clients.contains(apid))
							clients.add(apid);

					}

				} else if (nline.contains("Type/Subtype: Request-to-send")) {
					String readd = frames.get(i + 2);
					String[] b = readd.split(splitBy);
					String apid = b[1].substring(0, b[1].length() - 1);

					String tradd = frames.get(i + 3);
					String[] bb = tradd.split(splitBy);
					String apidd = bb[1].substring(0, bb[1].length() - 1);
					if (aps.contains(apid)) {
						if (!clients.contains(apidd))
							clients.add(apidd);
					} else if (aps.contains(apidd)) {
						if (!clients.contains(apid))
							clients.add(apid);
					}
				} else if (nline.contains("Type/Subtype: Authentication")) {
					String readd = frames.get(i + 3);
					String[] b = readd.split(splitBy);
					String apid = b[1].substring(0, b[1].length() - 1);

					String tradd = frames.get(i + 5);
					String[] bb = tradd.split(splitBy);
					String apidd = bb[1].substring(0, bb[1].length() - 1);

					String bsadd = frames.get(i + 6);
					String[] bbb = bsadd.split(splitBy);
					String apiddd = bbb[1].substring(0, bbb[1].length() - 1);
					if (apid.equalsIgnoreCase(apiddd)) {
						if (!clients.contains(apidd))
							clients.add(apidd);
					} else if (apidd.equalsIgnoreCase(apiddd)) {
						if (!clients.contains(apid))
							clients.add(apid);
					}
				} else if (nline.contains("Type/Subtype: Association Request")
						|| nline.contains("Type/Subtype: Reassociation Request")
						|| nline.contains("Type/Subtype: Probe Request")) {
					String sadd = frames.get(i + 5);
					String[] b = sadd.split(splitBy);
					String apid = b[1].substring(0, b[1].length() - 1);
					// System.out.println(apid);
					if (!clients.contains(apid))
						clients.add(apid);

					if (apsunique.contains(apid)) {
						long old_size = aps_sizes.get(apsunique.indexOf(apid));
						aps_sizes.set(apsunique.indexOf(apid), 1 + old_size);
					} else {
						apsunique.add(apid);
						aps_sizes.add(1l);
					}

				} else if (nline.contains("Type/Subtype: Association Response")
						|| nline.contains("Type/Subtype: Reassociation Response")
						|| nline.contains("Type/Subtype: Probe Response")) {
					String sadd = frames.get(i + 3);
					String[] b = sadd.split(splitBy);
					String apid = b[1].substring(0, b[1].length() - 1);
					if (!clients.contains(apid))
						clients.add(apid);
				}

			}

			long max_value = Collections.max(aps_sizes);
			for (int ii = 0; ii < aps_sizes.size(); ii++) {
				if (aps_sizes.get(ii) == max_value) {
					if (!clientsWithMax.contains(apsunique.get(ii)))
						clientsWithMax.add(apsunique.get(ii));
				}
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (clients.contains("ff:ff:ff:ff:ff:ff"))// broadcast message
		{
			clients.remove("ff:ff:ff:ff:ff:ff");
		}
		return clients.size();
	}

	public double findRetransmitted() {
		String splitBy = "\\=";
		int count = 0, repeat = 0;

		for (int i = 0; i < retrans.size(); i++) {
			String nline = retrans.get(i);
			if (nline.contains("Data frame")) {
				count++;
				String line = retrans.get(i + 1);
				String[] bb = line.split(splitBy);
				int retry = Integer.parseInt(bb[0].replace('.', ' ').trim());
				if (retry == 1)
					repeat++;
			}
		}

		return (double) repeat / count;
	}

	public double findfractionRC() {
		int count = 0, rtcs = 0;
		for (int i = 0; i < retrans.size(); i++) {
			String nline = retrans.get(i);
			if (nline.contains("Data frame"))
				count++;
		}
		rtcs = drc.size();
		 return (double) rtcs / count; 
		
	}

	public double findfractionOverhead() {

		double count[] = new double[9];
		double totaloverhead = 0;
		for (int i = 0; i < overhead.size(); i++) {
			String nline = overhead.get(i);
			if (nline.contains("Type/Subtype: Authentication"))
				count[5]++;
			else if (nline.contains("Type/Subtype: Association Response"))
				count[3]++;
			else if (nline.contains("Type/Subtype: Reassociation Response"))
				count[4]++;
			else if (nline.contains("Type/Subtype: Probe Response"))
				count[8]++;
			else if (nline.contains("Type/Subtype: Association Request"))
				count[1]++;
			else if (nline.contains("Type/Subtype: Reassociation Request"))
				count[2]++;
			else if (nline.contains("Type/Subtype: Probe Request"))
				count[7]++;
			else if (nline.contains("Type/Subtype: Beacon"))
				count[0]++;
			else if (nline.contains("Type/Subtype: Acknowledgement")) {
				count[6]++;

			}

		}

		for (double x : count)
			totaloverhead += x;

		int fullsize = overhead.size();
		System.out.println("Fraction of Beacon is : " + count[0] / fullsize
				* 100 + " %");
		System.out.println("Fraction of Association Request is : " + count[1]
				/ fullsize * 100 + " %");
		System.out.println("Fraction of Reassociation request is : " + count[2]
				/ fullsize * 100 + " %");
		System.out.println("Fraction of Association response is : " + count[3]
				/ fullsize * 100 + " %");
		System.out.println("Fraction of Reassociation response is : "
				+ count[4] / fullsize * 100 + " %");
		System.out.println("Fraction of Authentication is : " + count[5]
				/ fullsize * 100 + " %");
		System.out.println("Fraction of Acknowledgement is : " + count[6]
				/ fullsize * 100 + " %");
		System.out.println("Fraction of Probe Request is : " + count[7]
				/ fullsize * 100 + " %");
		System.out.println("Fraction of Probe Response is : " + count[8]
				/ fullsize * 100 + " %");

		return totaloverhead / fullsize;
	}

	public void findTotalUpDown() {
		int upcount = 0, downcount = 0;
		for (int i = 0; i < updown.size(); i++) {
			String nline = updown.get(i);

			if (nline.contains("Type: Data frame")) {

				String dsline = updown.get(i + 1);

				if (dsline.contains("10 = DS status: Frame from DS to a STA ")) {
					downcount++;
				} else if (dsline
						.contains("01 = DS status: Frame from STA to DS")) {
					upcount++;
				}
			}
		}
		int total = upcount + downcount;
		System.out.println("Total data frames :" + total);
		System.out.println("Fraction of Downlink is :"
				+ ((double) downcount / total) * 100 + " %");
		System.out.println("Fraction of Uplink is :"
				+ ((double) upcount / total) * 100 + " %");

	}
}
