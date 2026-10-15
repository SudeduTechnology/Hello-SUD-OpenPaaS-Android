package global.sud.gi.hello.app.game.op.repository;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import global.sud.op.runtime.core.listener.SUDOPGameListCallback;
import global.sud.op.runtime.core.model.SUDOPGameListItem;
import global.sud.op.runtime.core.model.SUDOPGameListParams;
import global.sud.op.runtime.core.model.SUDOPGameListResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OPRepositoryTest {

    @Test
    public void blankSelectionUsesOrdinaryListAndStartsAtPageZero() {
        FakeGateway gateway = new FakeGateway();
        gateway.pages.add(page(0, 1, item("game-1")));
        OPRepository repository = new OPRepository(gateway);
        List<SUDOPGameListItem> received = new ArrayList<>();

        repository.loadAll("  ", "zh-CN", new ResultCallback(received));

        assertEquals("", gateway.selectionIds.get(0));
        assertEquals(0, gateway.pageNumbers.get(0).intValue());
        assertEquals(OPRepository.PAGE_SIZE, gateway.pageSizes.get(0).intValue());
        assertEquals("zh-CN", gateway.languages.get(0));
        assertEquals(1, received.size());
    }

    @Test
    public void selectionListLoadsEveryPageAndDeduplicatesByGameId() {
        FakeGateway gateway = new FakeGateway();
        gateway.pages.add(page(0, 3, item("game-1"), item("game-2")));
        gateway.pages.add(page(1, 3, item("game-2"), item("game-3")));
        OPRepository repository = new OPRepository(gateway);
        List<SUDOPGameListItem> received = new ArrayList<>();

        repository.loadAll(" selection-1 ", "en-US", new ResultCallback(received));

        assertEquals(Arrays.asList("selection-1", "selection-1"), gateway.selectionIds);
        assertEquals(Arrays.asList(0, 1), gateway.pageNumbers);
        assertEquals(3, received.size());
        assertEquals("game-1", received.get(0).gameId);
        assertEquals("game-2", received.get(1).gameId);
        assertEquals("game-3", received.get(2).gameId);
    }

    @Test
    public void mismatchedPageNumberStopsPagination() {
        FakeGateway gateway = new FakeGateway();
        gateway.pages.add(page(1, 2, item("game-1")));
        OPRepository repository = new OPRepository(gateway);
        List<String> failures = new ArrayList<>();

        repository.loadAll("", "zh-CN", new OPRepository.Callback() {
            @Override
            public void onSuccess(List<SUDOPGameListItem> games) {
                throw new AssertionError("Unexpected success");
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                failures.add(retMsg);
            }
        });

        assertEquals(1, failures.size());
        assertTrue(failures.get(0).contains("page"));
    }

    private static SUDOPGameListResult page(int pageNo,
                                            int total,
                                            SUDOPGameListItem... items) {
        SUDOPGameListResult result = new SUDOPGameListResult();
        result.pageNo = pageNo;
        result.pageSize = OPRepository.PAGE_SIZE;
        result.total = total;
        result.records = Arrays.asList(items);
        return result;
    }

    private static SUDOPGameListItem item(String gameId) {
        SUDOPGameListItem item = new SUDOPGameListItem();
        item.gameId = gameId;
        return item;
    }

    private static class FakeGateway implements OPRepository.GameListGateway {
        final List<SUDOPGameListResult> pages = new ArrayList<>();
        final List<String> selectionIds = new ArrayList<>();
        final List<Integer> pageNumbers = new ArrayList<>();
        final List<Integer> pageSizes = new ArrayList<>();
        final List<String> languages = new ArrayList<>();

        @Override
        public void load(String selectionId,
                         SUDOPGameListParams params,
                         SUDOPGameListCallback callback) {
            selectionIds.add(selectionId);
            pageNumbers.add(params.pageNo);
            pageSizes.add(params.pageSize);
            languages.add(params.language);
            callback.onSuccess(pages.remove(0));
        }
    }

    private static class ResultCallback implements OPRepository.Callback {
        private final List<SUDOPGameListItem> target;

        ResultCallback(List<SUDOPGameListItem> target) {
            this.target = target;
        }

        @Override
        public void onSuccess(List<SUDOPGameListItem> games) {
            target.addAll(games);
        }

        @Override
        public void onFailure(int retCode, String retMsg) {
            throw new AssertionError(retCode + ":" + retMsg);
        }
    }
}
