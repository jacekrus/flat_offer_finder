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

	public void createOrUpdateOffersFileForId(String fileId, Collection<Offer> offers) throws IOException, OfferFinderException {
		File offersFile = getFileForId(fileId);
		if (offersFile.exists()) {
			Files.delete(offersFile.toPath());
		}
		writeOffersToFile(offersFile, offers);
	}

	public Collection<String> getOfferIdsForFileId(String fileId) throws OfferFinderException {
		File offersFile = getFileForId(fileId);
		if (offersFile.exists()) {
			try (Scanner scanner = new Scanner(offersFile)) {
				List<String> ids = new ArrayList<>();
				while (scanner.hasNextLine()) {
					String id = scanner.nextLine();
					ids.add(id);
				}
				return ids;
			} catch (Exception e) {
				throw new OfferFinderException("Exception occurred while processing offers file " + fileId + ".", e);
			}
		}
		return Collections.emptyList();
	}

	private File getFileForId(String id) {
		return new File(tempLocation + File.separator + id + ".txt");
	}

	private void writeOffersToFile(File file, Collection<Offer> offers) throws OfferFinderException {
		try (FileOutputStream fos = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
			for (Offer offer : offers) {
				bw.write(offer.getId());
				bw.newLine();
			}
		} catch (IOException e) {
			throw new OfferFinderException("Exception occurred while saving offers to file '" + file.getName() + "'.", e);
		}
	}

}
