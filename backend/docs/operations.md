# Local operations and production handoff

## Local run

```powershell
python -m pip install -e .
python -m skinproof.cli serve
```

The browser surface is `/`; interactive API documentation is `/docs`.

For development, omit `SKINPROOF_PHOTO_DIR` and the server uses memory-only
photo storage. For encrypted local object storage, set a 32-byte base64 key:

```powershell
$env:SKINPROOF_PHOTO_DIR = ".data/photos"
$env:SKINPROOF_PHOTO_KEY = "<base64-encoded-32-byte-key>"
python -m skinproof.cli serve
```

The `cryptography` dependency is declared in `pyproject.toml`; a deployment
must install project dependencies before enabling that store.

## PostgreSQL setup

Set DATABASE_URL in the process environment. The API opens a bounded
connection pool and applies every unapplied file in skinproof/migrations
before serving requests. Startup fails fast if the initial connection is
unavailable; /api/health reports database, database_ready, and returns 503
if a later health check fails.

For a reproducible local database and API, use:

docker compose up --build

For Neon, replace the local DATABASE_URL with the Neon pooled connection
string and keep the same application image; no service code changes are
required.

## Deployment checks

- provide DATABASE_URL and verify /api/health reports database_ready=true;
- use a managed PostgreSQL backup/restore policy and keep migration files in
  the release artifact;
- set SKINPROOF_DB_POOL_MAX_SIZE to match the host worker count and the
  provider connection limit;
- keep SKINPROOF_ENV=production and disable legacy local key-file lookup.

## Before accepting real users

- replace the memory/local store with S3/GCS + KMS and access audit logs;
- run a bounded raw-photo retention worker (the configured default is 730
  days) and keep derived metrics only after deletion policy review;
- put MediaPipe/ARKit capture guidance in the mobile client and validate one
  face, pose, distance, exposure, and reference-card state on-device;
- add a real queue worker and retry/dead-letter handling for analysis jobs;
- establish a diverse, consented longitudinal labeling workflow before
  training a custom blemish detector;
- add authenticated export packaging for raw objects and verify complete
  deletion across database, object storage, backups, and analytics copies;
- obtain dermatology/cosmetic-science and privacy/legal review before exposing
  new metrics or marketing language;
- add billing provider webhooks and entitlements without allowing commerce to
  influence verdict labels or placement.

## Health and evidence checks

- `GET /api/health` confirms the API process is live.
- `GET /api/users/{id}/dashboard` is the primary operator/user smoke check.
- Inspect `analysis_jobs` for failed processing and compare `model_version`
  before reprocessing historical photos.
- Treat a rise in `evidence_unclear` as a data-quality signal first: check
  cadence, capture quality, and simultaneous routine changes before changing
  thresholds.
