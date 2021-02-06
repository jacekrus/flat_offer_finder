package flat.offer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class OfferFileStorageHandler {

	private final String tempLocation;

	public OfferFileStorageHandler() {
		tempLocation = System.getProperty("java.io.tmpdir");
	}

	public void createOrUpdateOffersFileForSite(Site site, Collection<Offer> offers) throws IOException, OfferFinderException {
		File offersFile = getFileForSite(site);
		if (offersFile.exists()) {
			Files.delete(offersFile.toPath());
		}
		writeOffersToFile(offersFile, offers);
	}

	public Collection<String> getOfferIdsForSite(Site site) throws OfferFinderException {
		File offersFile = getFileForSite(site);
		if (offersFile.exists()) {
			try (Scanner scanner = new Scanner(offersFile)) {
				List<String> ids = new ArrayList<>();
				while (scanner.hasNextLine()) {
					String id = scanner.nextLine();
					ids.add(id);
				}
				return ids;
			} catch (Exception e) {
				throw new OfferFinderException("Exception occured while processing offers file for site " + site.toString() + ".", e);
			}
		}
		return Collections.emptyList();
	}

	private File getFileForSite(Site site) {
		return new File(tempLocation + File.separator + site.toString() + ".txt");
	}

	private void writeOffersToFile(File file, Collection<Offer> offers) throws OfferFinderException {
		try (FileOutputStream fos = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
			for (Offer offer : offers) {
				bw.write(offer.getId());
				bw.newLine();
			}
		} catch (IOException e) {
			throw new OfferFinderException("Exception occured while saving offers to file '" + file.getName() + "'.", e);
		}
	}

}
